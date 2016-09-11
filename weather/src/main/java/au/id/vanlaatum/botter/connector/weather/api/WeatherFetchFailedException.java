package au.id.vanlaatum.botter.connector.weather.api;

public class WeatherFetchFailedException extends Exception {
  public WeatherFetchFailedException () {
  }

  public WeatherFetchFailedException ( String message ) {
    super ( message );
  }

  public WeatherFetchFailedException ( String message, Throwable cause ) {
    super ( message, cause );
  }

  public WeatherFetchFailedException ( Throwable cause ) {
    super ( cause );
  }

  public WeatherFetchFailedException ( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
    super ( message, cause, enableSuppression, writableStackTrace );
  }
}
