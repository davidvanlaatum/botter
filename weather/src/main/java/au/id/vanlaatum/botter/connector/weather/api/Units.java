package au.id.vanlaatum.botter.connector.weather.api;

public enum Units {
  METRIC("C");

  private final String temperatureUnits;

  Units ( String temperatureUnits ) {
    this.temperatureUnits = temperatureUnits;
  }

  public String temperatureUnits () {
    return temperatureUnits;
  }
}
