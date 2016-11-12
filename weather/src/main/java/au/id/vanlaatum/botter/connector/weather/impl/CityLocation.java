package au.id.vanlaatum.botter.connector.weather.impl;

import au.id.vanlaatum.botter.connector.weather.api.WeatherLocation;
import au.id.vanlaatum.botter.connector.weather.api.WeatherLocationType;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

public class CityLocation implements WeatherLocation {
  private static final long serialVersionUID = 1;

  private String country;
  private String city;

  public CityLocation ( String country, String city ) {
    this.country = country;
    this.city = city;
  }

  public String getCountry () {
    return country;
  }

  public String getCity () {
    return city;
  }

  @Override
  public boolean equals ( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass () != o.getClass () ) {
      return false;
    }
    CityLocation that = (CityLocation) o;
    return Objects.equals ( country, that.country ) &&
        Objects.equals ( city, that.city );
  }

  @Override
  public int hashCode () {
    return Objects.hash ( country, city );
  }

  @Override
  public String toString () {
    return new ToStringBuilder ( this )
        .append ( "country", country )
        .append ( "city", city )
        .toString ();
  }

  @Override
  public WeatherLocationType getType () {
    return WeatherLocationType.CITY;
  }
}
