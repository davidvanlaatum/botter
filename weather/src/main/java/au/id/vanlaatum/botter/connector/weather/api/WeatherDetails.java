package au.id.vanlaatum.botter.connector.weather.api;

import java.math.BigDecimal;

public interface WeatherDetails {

  BigDecimal getTemperature ();

  String getCity ();

  String getCountry ();
}
