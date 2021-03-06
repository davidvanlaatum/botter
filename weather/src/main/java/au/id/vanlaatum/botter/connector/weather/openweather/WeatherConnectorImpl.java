package au.id.vanlaatum.botter.connector.weather.openweather;

import au.id.vanlaatum.botter.api.Attribute;
import au.id.vanlaatum.botter.api.BotFactory;
import au.id.vanlaatum.botter.api.CacheLookupException;
import au.id.vanlaatum.botter.connector.weather.api.UnsupportedLocationTypeException;
import au.id.vanlaatum.botter.connector.weather.api.WeatherConnector;
import au.id.vanlaatum.botter.connector.weather.api.WeatherDetails;
import au.id.vanlaatum.botter.connector.weather.api.WeatherFetchFailedException;
import au.id.vanlaatum.botter.connector.weather.api.WeatherLocation;
import au.id.vanlaatum.botter.connector.weather.api.WeatherSettings;
import au.id.vanlaatum.botter.connector.weather.impl.CityLocation;
import au.id.vanlaatum.botter.connector.weather.openweather.Model.CurrentWeather;
import au.id.vanlaatum.botter.connector.weather.openweather.Model.Forecast;
import au.id.vanlaatum.botter.connector.weather.openweather.Model.WeatherDetailsCurrentImpl;
import au.id.vanlaatum.botter.connector.weather.openweather.Model.WeatherDetailsForecast;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.ops4j.pax.cdi.api.OsgiService;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.Callable;

import static java.text.MessageFormat.format;
import static java.util.Objects.requireNonNull;

@Named ( "OpenWeatherConnector" )
@Singleton
public class WeatherConnectorImpl implements WeatherConnector, MetaTypeProvider, ManagedService {
  private static final URI base = URI.create ( "http://api.openweathermap.org/data/2.5/" );
  @Inject
  @Named ( "logService" )
  private LogService log;
  @Inject
  @OsgiService
  private BotFactory botFactory;
  @Inject
  @Named ( "httpClient" )
  private HttpClientBuilderFactory httpClientBuilderFactory;
  private boolean enabled;
  private String apiKey;
  private CloseableHttpClient httpClient;
  private ObjectMapper mapper = new ObjectMapper ();
  @Inject
  @Named ( "currentWeatherCache" )
  private CurrentWeatherCache currentWeatherCache;
  @Inject
  @Named ( "forecastWeatherCache" )
  private ForecastWeatherCache forecastWeatherCache;

  public WeatherConnectorImpl () throws URISyntaxException {
    mapper.disable ( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES );
  }

  public WeatherConnectorImpl setHttpClientBuilderFactory ( HttpClientBuilderFactory httpClientBuilderFactory ) {
    this.httpClientBuilderFactory = httpClientBuilderFactory;
    return this;
  }

  @Override
  public String getID () {
    return "OpenWeather";
  }

  @PostConstruct
  public void init () {
    httpClient = httpClientBuilderFactory.newBuilder ().build ();
  }

  @PreDestroy
  public void deinit () throws IOException {
    if ( httpClient != null ) {
      httpClient.close ();
    }
  }

  @Override
  public WeatherDetails getCurrentWeather ( WeatherLocation location, WeatherSettings settings ) throws WeatherFetchFailedException {
    if ( location == null ) {
      throw new WeatherFetchFailedException ( "No location specified" );
    }
    final List<BasicNameValuePair> parameters = new ArrayList<> ( Arrays.asList (
        new BasicNameValuePair ( "appid", apiKey ),
        new BasicNameValuePair ( "units", settings.getUnits ().toString () )
    ) );
    addLocationParameters ( parameters, location );
    final URI uri = base.resolve ( "weather?" + URLEncodedUtils.format ( parameters, "UTF-8" ) );
    try {
      return currentWeatherCache.lookup ( uri, new Callable<WeatherDetailsCurrentImpl> () {
        @Override
        public WeatherDetailsCurrentImpl call () throws Exception {
          log.log ( LogService.LOG_INFO, "Fetching weather details with " + uri.toString () );
          final HttpGet get = new HttpGet ( uri );
          try ( final CloseableHttpResponse response = httpClient.execute ( get ) ) {
            if ( response.getStatusLine ().getStatusCode () == HttpURLConnection.HTTP_OK ) {
              final CurrentWeather weather = mapper.readValue ( response.getEntity ().getContent (), CurrentWeather.class );
              if ( !weather.isSuccess () ) {
                throw new WeatherFetchFailedException ( format ( "{0}: {1}", weather.getCod (), weather.getMessage () ) );
              }
              return new WeatherDetailsCurrentImpl ( weather );
            } else {
              throw new WeatherFetchFailedException ( format ( "Got {0} from weather service", response.getStatusLine () ) );
            }
          } finally {
            get.releaseConnection ();
          }
        }
      } );
    } catch ( CacheLookupException e ) {
      throw new WeatherFetchFailedException ( e );
    }
  }

  @Override
  public WeatherDetails getDailyForecast ( WeatherLocation location, WeatherSettings settings, final Calendar date )
      throws WeatherFetchFailedException {
    if ( location == null ) {
      throw new WeatherFetchFailedException ( "No location specified" );
    }
    final List<BasicNameValuePair> parameters = new ArrayList<> ( Arrays.asList (
        new BasicNameValuePair ( "appid", apiKey ),
        new BasicNameValuePair ( "units", settings.getUnits ().toString () ),
        new BasicNameValuePair ( "cnt", "14" )
    ) );
    addLocationParameters ( parameters, location );
    final URI uri = base.resolve ( "forecast/daily?" + URLEncodedUtils.format ( parameters, "UTF-8" ) );
    try {
      return new WeatherDetailsForecast (
          requireNonNull ( forecastWeatherCache, "forecastWeatherCache is null" ).lookup ( uri, new Callable<Forecast> () {
            @Override
            public Forecast call () throws Exception {
              log.log ( LogService.LOG_INFO, "Fetching weather details with " + uri.toString () );
              final HttpGet get = new HttpGet ( uri );
              try ( final CloseableHttpResponse response = httpClient.execute ( get ) ) {
                if ( response.getStatusLine ().getStatusCode () == HttpURLConnection.HTTP_OK ) {
                  final Forecast weather = mapper.readValue ( response.getEntity ().getContent (), Forecast.class );
                  if ( !weather.isSuccess () ) {
                    throw new WeatherFetchFailedException ( format ( "{0}: {1}", weather.getCod (), weather.getMessage () ) );
                  }
                  return weather;
                } else {
                  throw new WeatherFetchFailedException ( format ( "Got {0} from weather service", response.getStatusLine () ) );
                }
              } finally {
                get.releaseConnection ();
              }
            }
          } ), date );
    } catch ( CacheLookupException e ) {
      throw new WeatherFetchFailedException ( e );
    }
  }

  private void addLocationParameters ( List<BasicNameValuePair> parameters, WeatherLocation location ) throws
      UnsupportedLocationTypeException {
    if ( location instanceof CityLocation ) {
      if ( ( (CityLocation) location ).getCountry () != null ) {
        parameters.add ( new BasicNameValuePair ( "q",
            format ( "{0},{1}", ( (CityLocation) location ).getCity (), ( (CityLocation) location ).getCountry () ) ) );
      } else {
        parameters.add ( new BasicNameValuePair ( "q", ( (CityLocation) location ).getCity () ) );
      }
    } else {
      throw new UnsupportedLocationTypeException ( location );
    }
  }

  @Override
  public boolean isEnabled () {
    return enabled && StringUtils.isNotBlank ( apiKey );
  }

  public WeatherConnectorImpl setEnabled ( boolean enabled ) {
    this.enabled = enabled;
    return this;
  }

  public String getApiKey () {
    return apiKey;
  }

  public WeatherConnectorImpl setApiKey ( String apiKey ) {
    this.apiKey = apiKey;
    return this;
  }

  @Override
  public ObjectClassDefinition getObjectClassDefinition ( String id, String locale ) {
    return new ObjectClassDefinition () {
      @Override
      public String getName () {
        return "Botter Open Weather";
      }

      @Override
      public String getID () {
        return WeatherConnector.class.getPackage ().getName ();
      }

      @Override
      public String getDescription () {
        return "Botter Open Weather";
      }

      @Override
      public AttributeDefinition[] getAttributeDefinitions ( int filter ) {
        List<AttributeDefinition> options = new ArrayList<> ();
        if ( filter == ALL || filter == REQUIRED ) {
          options.add ( new Attribute ().setId ( "api.key" ).setName ( "API Key" ).setType ( AttributeDefinition.STRING ) );
          options.add ( new Attribute ().setId ( "enabled" ).setName ( "Enabled" ).setType ( AttributeDefinition.BOOLEAN )
              .setDefaultValue ( "true" ) );
        }
        return options.toArray ( new AttributeDefinition[options.size ()] );
      }

      @Override
      public InputStream getIcon ( int size ) throws IOException {
        return null;
      }
    };
  }

  @Override
  public String[] getLocales () {
    return new String[]{ "en" };
  }

  @Override
  public void updated ( Dictionary<String, ?> dictionary ) throws ConfigurationException {
    if ( dictionary != null ) {
      enabled = (Boolean) dictionary.get ( "enabled" );
      apiKey = (String) dictionary.get ( "api.key" );
    } else {
      enabled = false;
      apiKey = null;
    }
  }

  public void setLog ( LogService log ) {
    this.log = log;
  }

  public WeatherConnectorImpl setCurrentWeatherCache ( CurrentWeatherCache currentWeatherCache ) {
    this.currentWeatherCache = currentWeatherCache;
    return this;
  }

  public WeatherConnectorImpl setForecastWeatherCache (
      ForecastWeatherCache forecastWeatherCache ) {
    this.forecastWeatherCache = forecastWeatherCache;
    return this;
  }
}
