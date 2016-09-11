package au.id.vanlaatum.botter.connector.weather.openweather;

import au.id.vanlaatum.botter.api.AbstractGenericCache;
import au.id.vanlaatum.botter.api.GenericCache;
import au.id.vanlaatum.botter.connector.weather.openweather.Model.WeatherDetailsImpl;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URI;

@Named ( "currentWeatherCache" )
@Singleton
@OsgiServiceProvider ( classes = GenericCache.class )
public class CurrentWeatherCache extends AbstractGenericCache<URI, WeatherDetailsImpl> {
  @Override
  public String getName () {
    return "Open Weather Current Weather";
  }
}
