package au.id.vanlaatum.botter.connector.weather.openweather;

import au.id.vanlaatum.botter.connector.weather.api.Units;
import au.id.vanlaatum.botter.connector.weather.api.WeatherDetails;
import au.id.vanlaatum.botter.connector.weather.api.WeatherSettings;
import au.id.vanlaatum.botter.connector.weather.impl.CityLocation;
import au.id.vanlaatum.botter.connector.weather.openweather.Model.CurrentWeatherTest;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.internal.stubbing.defaultanswers.TriesToReturnSelf;
import org.osgi.service.log.LogService;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class WeatherConnectorImplTest {
  @Test
  public void getCurrentWeather () throws Exception {
    WeatherConnectorImpl a = new WeatherConnectorImpl ();
    CloseableHttpClient client = mock ( CloseableHttpClient.class );
    CloseableHttpResponse response = mock ( CloseableHttpResponse.class );
    StatusLine statusLine = mock ( StatusLine.class );
    HttpEntity entity = mock ( HttpEntity.class );
    final HttpClientBuilder builder = mock ( HttpClientBuilder.class, new TriesToReturnSelf () );
    a.setHttpClientBuilderFactory ( new HttpClientBuilderFactory () {
      @Override
      public HttpClientBuilder newBuilder () {
        return builder;
      }
    } );
    when ( builder.build () ).thenReturn ( client );
    when ( client.execute ( org.mockito.Matchers.any ( HttpGet.class ) ) ).thenReturn ( response );
    when ( response.getStatusLine () ).thenReturn ( statusLine );
    when ( statusLine.getStatusCode () ).thenReturn ( 200 );
    when ( response.getEntity () ).thenReturn ( entity );
    when ( entity.getContent () ).thenReturn ( new ByteArrayInputStream ( CurrentWeatherTest.JSON.getBytes () ) );
    a.setApiKey ( "45cbe9327ad5919a698eaadcdabf9082" );
    a.setLog ( mock ( LogService.class ) );
    a.setCurrentWeatherCache ( new CurrentWeatherCache () );
    a.init ();
    final WeatherDetails currentWeather = a.getCurrentWeather ( new CityLocation ( "AU", "Adelaide" ), new WeatherSettings () {
      @Override
      public Units getUnits () {
        return Units.METRIC;
      }
    } );
    a.deinit ();
    assertNotNull ( currentWeather );
    assertNotNull ( currentWeather.getTemperature () );
    assertEquals ( new BigDecimal ( "14.72" ), currentWeather.getTemperature () );
  }
}
