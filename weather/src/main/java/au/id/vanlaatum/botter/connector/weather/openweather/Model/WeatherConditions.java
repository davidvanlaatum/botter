package au.id.vanlaatum.botter.connector.weather.openweather.Model;

import java.util.List;

public class WeatherConditions {
  private Integer id;
  private String main;
  private String description;
  private String icon;

  public static String toString ( List<WeatherConditions> weather ) {
    StringBuilder buffer = new StringBuilder ();
    for ( WeatherConditions conditions : weather ) {
      if ( buffer.length () > 0 ) {
        buffer.append ( ", " );
      }
      buffer.append ( conditions.getDescription () );
    }
    return buffer.toString ();
  }

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
