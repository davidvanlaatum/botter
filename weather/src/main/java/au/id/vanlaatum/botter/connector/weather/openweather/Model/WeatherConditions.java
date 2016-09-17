package au.id.vanlaatum.botter.connector.weather.openweather.Model;

public class WeatherConditions {
  private Integer id;
  private String main;
  private String description;
  private String icon;

  public Integer getId () {
    return id;
  }

  public WeatherConditions setId ( Integer id ) {
    this.id = id;
    return this;
  }

  public String getMain () {
    return main;
  }

  public WeatherConditions setMain ( String main ) {
    this.main = main;
    return this;
  }

  public String getDescription () {
    return description;
  }

  public WeatherConditions setDescription ( String description ) {
    this.description = description;
    return this;
  }

  public String getIcon () {
    return icon;
  }

  public WeatherConditions setIcon ( String icon ) {
    this.icon = icon;
    return this;
  }
}
