package au.id.vanlaatum.botter.connector.weather.api;

import java.math.BigDecimal;
import java.util.Calendar;

public interface WeatherDetails {

  BigDecimal getTemperature ();

  BigDecimal getTemperatureMin ();

  BigDecimal getTemperatureMax ();

  BigDecimal getWindSpeed ();

  Integer getWindDirection ();

  String getCity ();

  String getCountry ();

  BigDecimal getPressure ();

  Integer getHumidity ();

  Calendar getDate ();

  boolean isToday ();

  String getDescription ();
}
