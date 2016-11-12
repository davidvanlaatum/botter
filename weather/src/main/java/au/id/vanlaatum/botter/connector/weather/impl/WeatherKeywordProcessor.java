package au.id.vanlaatum.botter.connector.weather.impl;

import au.id.vanlaatum.botter.antlr.utils.CaseInsensitiveInputStream;
import au.id.vanlaatum.botter.antlr.utils.ErrorToExceptionErrorListener;
import au.id.vanlaatum.botter.api.BotFactory;
import au.id.vanlaatum.botter.api.Command;
import au.id.vanlaatum.botter.api.KeyWordProcessor;
import au.id.vanlaatum.botter.connector.weather.api.DirectionMap;
import au.id.vanlaatum.botter.connector.weather.api.Units;
import au.id.vanlaatum.botter.connector.weather.api.WeatherConnector;
import au.id.vanlaatum.botter.connector.weather.api.WeatherDetails;
import au.id.vanlaatum.botter.connector.weather.api.WeatherFetchFailedException;
import au.id.vanlaatum.botter.connector.weather.api.WeatherLocation;
import au.id.vanlaatum.botter.connector.weather.api.WeatherLocationType;
import au.id.vanlaatum.botter.connector.weather.api.WeatherSettings;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.NoViableAltException;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static au.id.vanlaatum.botter.connector.weather.api.WeatherLocationType.CITY;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

@Named ( "WeatherKeyword" )
@Singleton
public class WeatherKeywordProcessor implements KeyWordProcessor, ManagedService {
  static final String USER_SETTING_COUNTRY = "country";
  static final String USER_SETTING_CITY = "city";
  static final String DEFAULT_LOCATION_TYPE = "location.default.type";
  static final String DEFAULT_LOCATION_COUNTRY = "location.default.country";
  static final String DEFAULT_LOCATION_CITY = "location.default.city";
  private static final Map<Integer, String> daysOfWeek = new TreeMap<> ();

  static {
    daysOfWeek.put ( Calendar.MONDAY, "Monday" );
    daysOfWeek.put ( Calendar.TUESDAY, "Tuesday" );
    daysOfWeek.put ( Calendar.WEDNESDAY, "Wednesday" );
    daysOfWeek.put ( Calendar.THURSDAY, "Thursday" );
    daysOfWeek.put ( Calendar.FRIDAY, "Friday" );
    daysOfWeek.put ( Calendar.SATURDAY, "Saturday" );
    daysOfWeek.put ( Calendar.SUNDAY, "Sunday" );
  }

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
  @Inject
  @Named ( "directionMapImpl" )
  private DirectionMap directionMap;

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
      log.log ( ex.getCause () instanceof LexerNoViableAltException || ex.getCause () instanceof NoViableAltException ?
          LogService.LOG_DEBUG : LogService.LOG_INFO, null, ex );
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
    Calendar date = null;
    final WeatherSettings settings = determineWeatherSettings ( message );
    switch ( question.getTimeType () ) {
      case TODAY:
        for ( WeatherConnector connector : getRelevantConnectors ( message ) ) {
          try {
            final WeatherDetails currentWeather = connector.getCurrentWeather ( determineLocation ( question, message ), settings );
            replyWithDetails ( currentWeather, question, settings, message );
            exceptions.clear ();
            break;
          } catch ( WeatherFetchFailedException ex ) {
            log.log ( LogService.LOG_WARNING, null, ex );
            exceptions.add ( ex );
          }
        }
        break;
      case TOMORROW:
        date = (Calendar) Calendar.getInstance ( message.getUser ().getTimezone () ).clone ();
        date.add ( Calendar.DAY_OF_MONTH, 1 );
        break;
      case DOW:
        date = (Calendar) Calendar.getInstance ( message.getUser ().getTimezone () ).clone ();
        date.set ( Calendar.DAY_OF_WEEK, question.getDow () );
        if ( date.before ( Calendar.getInstance () ) ) {
          date.add ( Calendar.DAY_OF_MONTH, 7 );
        }
        break;
      case DATE:
        date = (Calendar) Calendar.getInstance ( message.getUser ().getTimezone () ).clone ();
        date.setTime ( question.getDate () );
        break;
      default:
        throw new WeatherFetchFailedException ( "Unknown time " + question.getTimeType () );
    }

    if ( date != null ) {
      date.set ( Calendar.HOUR_OF_DAY, 0 );
      date.clear ( Calendar.MINUTE );
      date.clear ( Calendar.SECOND );
      date.clear ( Calendar.MILLISECOND );
      for ( WeatherConnector connector : getRelevantConnectors ( message ) ) {
        try {
          final WeatherDetails details = connector.getDailyForecast ( determineLocation ( question, message ), settings, date );
          replyWithDetails ( details, question, settings, message );
          exceptions.clear ();
          break;
        } catch ( WeatherFetchFailedException ex ) {
          log.log ( LogService.LOG_WARNING, null, ex );
          exceptions.add ( ex );
        }
      }
    }

    if ( !exceptions.isEmpty () ) {
      StringBuilder buffer = new StringBuilder ();
      for ( WeatherFetchFailedException exception : exceptions ) {
        buffer.append ( "\n " ).append ( exception.getMessage () );
      }
      message.reply ( format ( "failed to fetch info: {0}", buffer ) );
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

  void replyWithDetails ( WeatherDetails weather, WeatherQuestion question, WeatherSettings settings, Command message ) {
    switch ( question.getSubject () ) {
      case TEMPERATURE:
        if ( weather.isToday () ) {
          message.reply ( format ( "The temperature is currently {0}{1} with a max of {4}{1} and a min of {5}{1} in {2}, {3}",
              weather.getTemperature (), settings.getUnits ().temperatureUnits (), weather.getCity (),
              weather.getCountry (), weather.getTemperatureMax (), weather.getTemperatureMin () ) );
        } else {
          message.reply ( format ( "The temperature {0} will be between {1}{5} and {2}{5} in {3}, {4}",
              dateToString ( weather.getDate () ), weather.getTemperatureMin (), weather.getTemperatureMax (),
              weather.getCity (), weather.getCountry (), settings.getUnits ().temperatureUnits () ) );
        }
        break;
      case WEATHER:
        StringBuilder buffer = new StringBuilder ();
        buffer.append ( format ( "In {0}, {1}: {2}\n", weather.getCity (), weather.getCountry (), weather.getDescription () ) );
        if ( weather.isToday () ) {
          buffer.append ( format ( "\tTemperature is currently {0}{1}", weather.getTemperature (),
              settings.getUnits ().temperatureUnits () ) );
        } else {
          buffer.append ( "\tTemperature" );
        }
        if ( weather.getTemperatureMax () != null ) {
          buffer.append ( " min: " ).append ( weather.getTemperatureMin () ).append ( settings.getUnits ().temperatureUnits () );
          buffer.append ( " max: " ).append ( weather.getTemperatureMax () ).append ( settings.getUnits ().temperatureUnits () );
        }
        buffer.append ( "\n" );
        if ( weather.getWindSpeed () != null ) {
          buffer.append ( "\tWind speed " ).append ( weather.getWindSpeed () ).append ( settings.getUnits ().speedUnits () );
          if ( weather.getWindDirection () != null ) {
            buffer.append ( " " ).append ( directionMap.getDescriptionForDegrees ( new BigDecimal ( weather.getWindDirection () ) ) );
          }
          buffer.append ( "\n" );
        }
        if ( weather.getHumidity () != null ) {
          buffer.append ( "\tHumidity " ).append ( weather.getHumidity () ).append ( "%" ).append ( "\n" );
        }
        if ( weather.getPressure () != null ) {
          buffer.append ( "\tPressure " ).append ( weather.getPressure () ).append ( settings.getUnits ().pressureUnits () )
              .append ( "\n" );
        }
        message.reply ( buffer.toString ().trim () );
        break;
    }
  }

  String dateToString ( Calendar date ) {
    final Calendar today = Calendar.getInstance ( date.getTimeZone () );
    today.set ( Calendar.HOUR_OF_DAY, 0 );
    today.clear ( Calendar.MINUTE );
    today.clear ( Calendar.SECOND );
    Calendar tomorrow = (Calendar) today.clone ();
    tomorrow.roll ( Calendar.DAY_OF_MONTH, true );
    Calendar nextDay = (Calendar) tomorrow.clone ();
    nextDay.roll ( Calendar.DAY_OF_MONTH, true );
    Calendar nextWeek = (Calendar) today.clone ();
    nextWeek.roll ( Calendar.DAY_OF_MONTH, 7 );

    if ( date.compareTo ( today ) >= 0 && date.compareTo ( tomorrow ) < 0 ) {
      return "today";
    } else if ( date.compareTo ( tomorrow ) >= 0 && date.compareTo ( nextDay ) < 0 ) {
      return "tomorrow";
    } else if ( date.compareTo ( today ) >= 0 && date.compareTo ( nextWeek ) < 0 ) {
      return "on " + daysOfWeek.get ( date.get ( Calendar.DAY_OF_WEEK ) );
    } else {
      return String.format ( "on %04d-%02d-%02d", date.get ( Calendar.YEAR ), date.get ( Calendar.MONTH ) + 1,
          date.get ( Calendar.DAY_OF_MONTH ) );
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
    WeatherLocation location;
    if ( isNoneBlank ( question.getCity () ) ) {
      location = new CityLocation ( question.getCountry (), question.getCity () );
    } else {
      location = getUsersLocation ( message.getUser ().getUniqID () );
    }
    return location;
  }

  WeatherLocation getUsersLocation ( String uniqID ) {
    final Preferences userPreferences = preferencesService.getUserPreferences ( uniqID );
    WeatherLocation location;
    if ( isNoneBlank ( userPreferences.get ( USER_SETTING_CITY, null ) ) ) {
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
    lexer.addErrorListener ( new ErrorToExceptionErrorListener () );
    parser.setErrorHandler ( new BailErrorStrategy () );
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
          final QuestionParser.SetlocationContext setlocation = question.setlocation ();
          rt = new SetLocation ( setlocation.city, setlocation.country );
          break;
        default:
          throw new UnsupportedOperationException ( "Unsupported question type " + question.type );
      }
    }
    return rt;
  }

  public WeatherKeywordProcessor setConnectors ( List<WeatherConnector> connectors ) {
    this.connectors = new ArrayList<> ( connectors );
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
    WeatherLocationType type =
        WeatherLocationType.valueOf ( defaultIfBlank ( getSetting ( dictionary, DEFAULT_LOCATION_TYPE ), CITY.toString () ) );
    switch ( type ) {
      case CITY:
        defaultLocation = new CityLocation ( defaultIfBlank ( getSetting ( dictionary, DEFAULT_LOCATION_COUNTRY ), "Australia" ),
            defaultIfBlank ( getSetting ( dictionary, DEFAULT_LOCATION_CITY ), "Adelaide" ) );
        break;
      default:
        log.log ( LogService.LOG_ERROR, "Unknown location type " + type );
    }
  }

  public void setDirectionMap ( DirectionMapImpl directionMap ) {
    this.directionMap = directionMap;
  }

  public WeatherLocation getDefaultLocation () {
    return defaultLocation;
  }
}
