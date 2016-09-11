package au.id.vanlaatum.botter.connector.weather.openweather;

import au.id.vanlaatum.botter.connector.weather.api.Units;
import au.id.vanlaatum.botter.connector.weather.api.WeatherDetails;
import au.id.vanlaatum.botter.connector.weather.api.WeatherSettings;
import au.id.vanlaatum.botter.connector.weather.impl.CityLocation;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WeatherConnectorImplTest {
  @Test
  public void getCurrentWeather () throws Exception {
    WeatherConnectorImpl a = new WeatherConnectorImpl ();
    a.setHttpClientBuilderFactory ( new HttpClientBuilderFactory () {
      @Override
      public HttpClientBuilder newBuilder () {
        return HttpClientBuilder.create ();
      }
    } );
    a.init ();
    a.setApiKey ( "45cbe9327ad5919a698eaadcdabf9082" );
    final WeatherDetails currentWeather = a.getCurrentWeather ( new CityLocation ( "AU", "Adelaide" ), new WeatherSettings () {
      @Override
      public Units getUnits () {
        return Units.METRIC;
      }
    } );
    a.deinit ();
    assertNotNull ( currentWeather );
    assertNotNull ( currentWeather.getTemperature () );
    assertEquals ( new BigDecimal ( 13.62 ), currentWeather.getTemperature () );
  }
}
