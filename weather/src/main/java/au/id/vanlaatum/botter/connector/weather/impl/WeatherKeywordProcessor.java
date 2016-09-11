package au.id.vanlaatum.botter.connector.weather.impl;

import au.id.vanlaatum.botter.api.BotFactory;
import au.id.vanlaatum.botter.api.Command;
import au.id.vanlaatum.botter.api.KeyWordProcessor;
import au.id.vanlaatum.botter.connector.weather.api.Units;
import au.id.vanlaatum.botter.connector.weather.api.WeatherConnector;
import au.id.vanlaatum.botter.connector.weather.api.WeatherDetails;
import au.id.vanlaatum.botter.connector.weather.api.WeatherFetchFailedException;
import au.id.vanlaatum.botter.connector.weather.api.WeatherLocation;
import au.id.vanlaatum.botter.connector.weather.api.WeatherLocationType;
import au.id.vanlaatum.botter.connector.weather.api.WeatherSettings;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;

import static java.text.MessageFormat.format;


@Named ( "WeatherKeyword" )
@Singleton
public class WeatherKeywordProcessor implements KeyWordProcessor, ANTLRErrorListener, ManagedService {
  private static final String USER_SETTING_COUNTRY = "country";
  private static final String USER_SETTING_CITY = "city";
  private static final String DEFAULT_LOCATION_TYPE = "location.default.type";
  private static final String DEFAULT_LOCATION_COUNTRY = "location.default.country";
  private static final String DEFAULT_LOCATION_CITY = "location.default.city";
  @Inject
  @Named ( "Connectors" )
  private List<WeatherConnector> connectors;
  @Inject
  @Named ( "logService" )
  private LogService log;
  @Inject
  @OsgiService
  private BotFactory botFactory;
  @Inject
  @OsgiService
  private PreferencesService preferencesService;
  private WeatherLocation defaultLocation;

  @Override
  public boolean checkForKeywords ( final Command message, List<Boolean> used ) {
    try {
      final Question question = parseQuestion ( message.getCommandText () );
      if ( question instanceof WeatherQuestion ) {
        weatherQuestion ( message, (WeatherQuestion) question );
      } else if ( question instanceof SetLocation ) {
        setLocation ( (SetLocation) question, message );
      }
      return question != null;
    } catch ( Exception ex ) {
      log.log ( LogService.LOG_INFO, null, ex );
      return false;
    }
  }

  private List<WeatherConnector> getRelevantConnectors ( Command message ) {
    List<WeatherConnector> rt = new ArrayList<> ();
    final Preferences userPreferences = preferencesService.getUserPreferences ( message.getUser ().getUniqID () );
    final String connectorID = userPreferences.get ( "connector", null );
    for ( WeatherConnector connector : connectors ) {
      if ( ( connectorID == null || Objects.equals ( connectorID, connector.getID () ) ) && connector.isEnabled () ) {
        rt.add ( connector );
      }
    }
    return rt;
  }

  void weatherQuestion ( Command message, WeatherQuestion question ) throws WeatherFetchFailedException {
    List<WeatherFetchFailedException> exceptions = new ArrayList<> ();
    switch ( question.getTimeType () ) {
      case TODAY:
      default:
        for ( WeatherConnector connector : getRelevantConnectors ( message ) ) {
          try {
            final WeatherSettings settings = determineWeatherSettings ( message );
            final WeatherDetails currentWeather =
                connector.getCurrentWeather ( determineLocation ( question, message ), settings );
            replyWithDetails ( currentWeather, question, settings, message );
            exceptions.clear ();
          } catch ( WeatherFetchFailedException ex ) {
            exceptions.add ( ex );
          }
          break;
        }
        break;
    }

    if ( !exceptions.isEmpty () ) {
      StringBuilder buffer = new StringBuilder ();
      for ( WeatherFetchFailedException exception : exceptions ) {
        buffer.append ( "\n " ).append ( exception.getMessage () );
      }
      message.reply ( format ( "failed to fetch info: {0}", buffer.toString () ) );
    }
  }

  void setLocation ( SetLocation question, Command message ) throws BackingStoreException {
    final Preferences userPreferences = preferencesService.getUserPreferences ( message.getUser ().getUniqID () );
    userPreferences.put ( USER_SETTING_CITY, question.getCity () );
    if ( question.getCountry () != null ) {
      userPreferences.put ( USER_SETTING_COUNTRY, question.getCountry () );
    } else {
      userPreferences.remove ( USER_SETTING_COUNTRY );
    }
    userPreferences.sync ();
    message.reply ( "Location updated" );
  }

  void replyWithDetails ( WeatherDetails currentWeather, WeatherQuestion question, WeatherSettings settings, Command message ) {
    switch ( question.getSubject () ) {
      case TEMPERATURE:
        message.reply ( format ( "The temperature is currently {0}{1} in {2}, {3}", currentWeather.getTemperature (),
            settings.getUnits ().temperatureUnits (), currentWeather.getCity (), currentWeather.getCountry () ) );
        break;
    }
  }

  private WeatherSettings determineWeatherSettings ( Command message ) {
    return new WeatherSettings () {
      @Override
      public Units getUnits () {
        return Units.METRIC;
      }
    };
  }

  WeatherLocation determineLocation ( WeatherQuestion question, final Command message ) {
    WeatherLocation location = null;
    if ( question.getCity () != null && !question.getCity ().isEmpty () ) {
      location = new CityLocation ( question.getCountry (), question.getCity () );
    } else {
      location = getUsersLocation ( message.getUser ().getUniqID () );
    }
    return location;
  }

  WeatherLocation getUsersLocation ( String uniqID ) {
    final Preferences userPreferences = preferencesService.getUserPreferences ( uniqID );
    WeatherLocation location = null;
    if ( userPreferences.get ( USER_SETTING_CITY, null ) != null ) {
      location = new CityLocation ( userPreferences.get ( USER_SETTING_COUNTRY, null ), userPreferences.get ( USER_SETTING_CITY, null ) );
    } else {
      location = defaultLocation;
    }

    return location;
  }

  Question parseQuestion ( String text ) {
    QuestionLexer lexer = new QuestionLexer ( new CaseInsensitiveInputStream ( text ) );
    QuestionParser parser = new QuestionParser ( new BufferedTokenStream ( lexer ) );
    lexer.removeErrorListeners ();
    lexer.addErrorListener ( this );
    parser.removeErrorListeners ();
    parser.addErrorListener ( this );
    QuestionParser.QuestionContext question = parser.question ();
    Question rt = null;
    if ( question != null && question.exception == null ) {
      switch ( question.type ) {
        case WEATHER:
          final QuestionParser.WetherquestionContext wetherquestion = question.wetherquestion ();
          rt = new WeatherQuestion ( wetherquestion.valueTime, wetherquestion.valueDow, wetherquestion.valueDate,
              wetherquestion.valueSubject, wetherquestion.city, wetherquestion.country );
          break;
        case SETLOCATION:
          rt = new SetLocation ( question.setlocation ().city, question.setlocation ().country );
          break;
      }
    }
    return rt;
  }

  private void log ( int level, String msg, Exception ex ) {
    if ( log != null ) {
      log.log ( level, msg, ex );
    } else {
      if ( msg != null ) {
        System.out.println ( format ( "{0}: {1}", level, msg ) );
      }
      if ( ex != null ) {
        ex.printStackTrace ( System.out );
      }
    }
  }

  public WeatherKeywordProcessor setConnectors ( List<WeatherConnector> connectors ) {
    this.connectors = connectors;
    return this;
  }

  public WeatherKeywordProcessor setLog ( LogService log ) {
    this.log = log;
    return this;
  }

  public WeatherKeywordProcessor setPreferencesService ( PreferencesService preferencesService ) {
    this.preferencesService = preferencesService;
    return this;
  }

  @Override
  public void syntaxError ( Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg,
                            RecognitionException e ) {
    throw new RuntimeException ( line + ":" + charPositionInLine + ' ' + msg, e );
  }

  @Override
  public void reportAmbiguity ( Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts,
                                ATNConfigSet configs ) {

  }

  @Override
  public void reportAttemptingFullContext ( Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts,
                                            ATNConfigSet configs ) {

  }

  @Override
  public void reportContextSensitivity ( Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs ) {

  }

  <T> T getSetting ( Dictionary<String, ?> dictionary, String name, Class<T> type ) {
    if ( dictionary != null ) {
      return (T) dictionary.get ( name );
    } else {
      return null;
    }
  }

  String getSetting ( Dictionary<String, ?> dictionary, String name ) {
    return getSetting ( dictionary, name, String.class );
  }

  @Override
  public void updated ( Dictionary<String, ?> dictionary ) throws ConfigurationException {
    WeatherLocationType type = WeatherLocationType.valueOf ( StringUtils.defaultIfBlank ( getSetting ( dictionary, DEFAULT_LOCATION_TYPE ),
        WeatherLocationType.CITY.toString () ) );
    switch ( type ) {
      case CITY:
        defaultLocation =
            new CityLocation ( StringUtils.defaultIfBlank ( getSetting ( dictionary, DEFAULT_LOCATION_COUNTRY ), "Australia" ),
                StringUtils.defaultIfBlank ( getSetting ( dictionary, DEFAULT_LOCATION_CITY ), "Adelaide" ) );
        break;
      default:
        log.log ( LogService.LOG_ERROR, "Unknown location type " + type );
    }
  }
}