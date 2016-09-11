package au.id.vanlaatum.botter.connector.weather.api;

public enum Units {
  METRIC ( "°C", "KM/H" );

  private final String temperatureUnits;
  private final String speedUnits;

  Units ( String temperatureUnits, String speedUnits ) {
    this.temperatureUnits = temperatureUnits;
    this.speedUnits = speedUnits;
  }

  public String temperatureUnits () {
    return temperatureUnits;
  }

  public String speedUnits () {
    return speedUnits;
  }
}
