package au.id.vanlaatum.botter.connector.weather.api;

public enum Units {
  METRIC ( "°C", "KM/H", "hPa" );

  private final String temperatureUnits;
  private final String speedUnits;
  private final String pressureUnits;

  Units ( String temperatureUnits, String speedUnits, String presureUnits ) {
    this.temperatureUnits = temperatureUnits;
    this.speedUnits = speedUnits;
    this.pressureUnits = presureUnits;
  }

  public String temperatureUnits () {
    return temperatureUnits;
  }

  public String speedUnits () {
    return speedUnits;
  }

  public String pressureUnits () {
    return pressureUnits;
  }
}
