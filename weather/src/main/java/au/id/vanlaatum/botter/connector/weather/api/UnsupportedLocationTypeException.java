package au.id.vanlaatum.botter.connector.weather.api;

public class UnsupportedLocationTypeException extends WeatherFetchFailedException {
  private final WeatherLocation location;

  public UnsupportedLocationTypeException ( WeatherLocation location ) {
    super ( "Unsupported location type " + ( location != null ? location.getClass ().getName () : "null" ) );
    this.location = location;
  }

  public WeatherLocation getLocation () {
    return location;
  }
}
