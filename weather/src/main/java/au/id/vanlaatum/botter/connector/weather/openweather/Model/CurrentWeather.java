package au.id.vanlaatum.botter.connector.weather.openweather.Model;

import au.id.vanlaatum.botter.connector.weather.openweather.UnixTimestampDeserializer;
import au.id.vanlaatum.botter.connector.weather.openweather.UnixTimestampSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CurrentWeather extends BaseResponse {
  private Long id;
  private String name;
  private Coord coord;
  private List<WeatherConditions> weather;
  private String base;
  private Main main;
  private Long visibility;
  private Wind wind;
  private Clouds clouds;
  @JsonDeserialize ( using = UnixTimestampDeserializer.class )
  @JsonSerialize ( using = UnixTimestampSerializer.class )
  private Date dt;
  private Sys sys;

  public Coord getCoord () {
    return coord;
  }

  public CurrentWeather setCoord ( Coord coord ) {
    this.coord = coord;
    return this;
  }

  public List<WeatherConditions> getWeather () {
    return Collections.unmodifiableList ( weather );
  }

  public CurrentWeather setWeather ( List<WeatherConditions> weather ) {
    this.weather = new ArrayList<> ( weather );
    return this;
  }

  public String getBase () {
    return base;
  }

  public CurrentWeather setBase ( String base ) {
    this.base = base;
    return this;
  }

  public Main getMain () {
    return main;
  }

  public CurrentWeather setMain ( Main main ) {
    this.main = main;
    return this;
  }

  public Long getVisibility () {
    return visibility;
  }

  public CurrentWeather setVisibility ( Long visibility ) {
    this.visibility = visibility;
    return this;
  }

  public Long getId () {
    return id;
  }

  public CurrentWeather setId ( Long id ) {
    this.id = id;
    return this;
  }

  public String getName () {
    return name;
  }

  public CurrentWeather setName ( String name ) {
    this.name = name;
    return this;
  }

  public Wind getWind () {
    return wind;
  }

  public CurrentWeather setWind ( Wind wind ) {
    this.wind = wind;
    return this;
  }

  public Clouds getClouds () {
    return clouds;
  }

  public CurrentWeather setClouds ( Clouds clouds ) {
    this.clouds = clouds;
    return this;
  }

  public Date getDt () {
    return ObjectUtils.clone ( dt );
  }

  public CurrentWeather setDt ( Date dt ) {
    this.dt = ObjectUtils.clone ( dt );
    return this;
  }

  public Sys getSys () {
    return sys;
  }

  public CurrentWeather setSys ( Sys sys ) {
    this.sys = sys;
    return this;
  }

  public static class Coord {
    private BigDecimal lon;
    private BigDecimal lat;

    public BigDecimal getLon () {
      return lon;
    }

    public Coord setLon ( BigDecimal lon ) {
      this.lon = lon;
      return this;
    }

    public BigDecimal getLat () {
      return lat;
    }

    public Coord setLat ( BigDecimal lat ) {
      this.lat = lat;
      return this;
    }
  }

  public static class Main {
    private BigDecimal temp;
    private BigDecimal pressure;
    private Integer humidity;
    private BigDecimal tempMin;
    private BigDecimal tempMax;


    public BigDecimal getTemp () {
      return temp;
    }

    public Main setTemp ( BigDecimal temp ) {
      this.temp = temp;
      return this;
    }

    public BigDecimal getPressure () {
      return pressure;
    }

    public Main setPressure ( BigDecimal pressure ) {
      this.pressure = pressure;
      return this;
    }

    public Integer getHumidity () {
      return humidity;
    }

    public Main setHumidity ( Integer humidity ) {
      this.humidity = humidity;
      return this;
    }

    @JsonProperty ( "temp_min" )
    public BigDecimal getTempMin () {
      return tempMin;
    }

    public Main setTempMin ( BigDecimal tempMin ) {
      this.tempMin = tempMin;
      return this;
    }

    @JsonProperty ( "temp_max" )
    public BigDecimal getTempMax () {
      return tempMax;
    }

    public Main setTempMax ( BigDecimal tempMax ) {
      this.tempMax = tempMax;
      return this;
    }
  }

  public static class Wind {
    private BigDecimal speed;
    private Integer deg;

    public BigDecimal getSpeed () {
      return speed;
    }

    public Wind setSpeed ( BigDecimal speed ) {
      this.speed = speed;
      return this;
    }

    public Integer getDeg () {
      return deg;
    }

    public Wind setDeg ( Integer deg ) {
      this.deg = deg;
      return this;
    }
  }

  public static class Clouds {
    private Integer all;

    public Integer getAll () {
      return all;
    }

    public Clouds setAll ( Integer all ) {
      this.all = all;
      return this;
    }
  }

  public static class Sys {
    private Integer type;
    private Integer id;
    private BigDecimal message;
    private String country;
    @JsonDeserialize ( using = UnixTimestampDeserializer.class )
    @JsonSerialize ( using = UnixTimestampSerializer.class )
    private Date sunrise;
    @JsonDeserialize ( using = UnixTimestampDeserializer.class )
    @JsonSerialize ( using = UnixTimestampSerializer.class )
    private Date sunset;

    public Integer getType () {
      return type;
    }

    public Sys setType ( Integer type ) {
      this.type = type;
      return this;
    }

    public Integer getId () {
      return id;
    }

    public Sys setId ( Integer id ) {
      this.id = id;
      return this;
    }

    public BigDecimal getMessage () {
      return message;
    }

    public Sys setMessage ( BigDecimal message ) {
      this.message = message;
      return this;
    }

    public String getCountry () {
      return country;
    }

    public Sys setCountry ( String country ) {
      this.country = country;
      return this;
    }

    public Date getSunrise () {
      return ObjectUtils.clone ( sunrise );
    }

    public Sys setSunrise ( Date sunrise ) {
      this.sunrise = ObjectUtils.clone ( sunrise );
      return this;
    }

    public Date getSunset () {
      return ObjectUtils.clone ( sunset );
    }

    public Sys setSunset ( Date sunset ) {
      this.sunset = ObjectUtils.clone ( sunset );
      return this;
    }
  }
}
