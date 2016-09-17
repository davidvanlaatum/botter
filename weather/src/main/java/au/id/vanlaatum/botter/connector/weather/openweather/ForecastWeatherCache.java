package au.id.vanlaatum.botter.connector.weather.openweather;

import au.id.vanlaatum.botter.api.AbstractGenericCache;
import au.id.vanlaatum.botter.api.GenericCache;
import au.id.vanlaatum.botter.connector.weather.openweather.Model.Forecast;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URI;

@Named ( "forecastWeatherCache" )
@Singleton
@OsgiServiceProvider ( classes = GenericCache.class )
public class ForecastWeatherCache extends AbstractGenericCache<URI, Forecast> {

  @Override
  public String getName () {
    return "Open Weather Weather Forecast";
  }
}
