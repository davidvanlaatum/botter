package au.id.vanlaatum.botter.connector.weather.api;

import java.math.BigDecimal;

public interface WeatherDetails {

  BigDecimal getTemperature ();

  BigDecimal getTemperatureMin ();

  BigDecimal getTemperatureMax ();

  BigDecimal getWindSpeed ();

  Integer getWindDirection ();

  String getCity ();

  String getCountry ();

  Integer getPressure ();

  Integer getHumidity ();
}
