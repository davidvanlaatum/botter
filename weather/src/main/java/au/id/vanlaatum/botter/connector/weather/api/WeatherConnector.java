package au.id.vanlaatum.botter.connector.weather.api;

import java.util.Calendar;

public interface WeatherConnector {
  String getID ();

  WeatherDetails getCurrentWeather ( WeatherLocation location, WeatherSettings settings ) throws WeatherFetchFailedException;

  boolean isEnabled ();

  WeatherDetails getDailyForecast ( WeatherLocation weatherLocation, WeatherSettings settings, Calendar date )
      throws WeatherFetchFailedException;
}
