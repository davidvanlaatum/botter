package au.id.vanlaatum.botter.connector.weather.api;

public interface WeatherConnector {
  String getID ();

  WeatherDetails getCurrentWeather ( WeatherLocation location, WeatherSettings settings ) throws WeatherFetchFailedException;

  boolean isEnabled();
}
