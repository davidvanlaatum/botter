package au.id.vanlaatum.botter.connector.weather.api;

import java.io.Serializable;

public interface WeatherLocation extends Serializable {
  WeatherLocationType getType ();
}
