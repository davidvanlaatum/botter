package au.id.vanlaatum.botter.connector.weather.openweather.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CurrentWeather {
  private Long id;
  private String name;
  private Integer cod;
  private Coord coord;
  private List<Weather> weather;
  private String base;
  private Main main;
  private Long visibility;
  private Wind wind;
  private Clouds clouds;
  private Date dt;
  private Sys sys;
  private String message;

  public String getMessage () {
    return message;
  }

  public CurrentWeather setMessage ( String message ) {
    this.message = message;
    return this;
  }

  public Coord getCoord () {
    return coord;
  }

  public CurrentWeather setCoord ( Coord coord ) {
    this.coord = coord;
    return this;
  }

  public List<Weather> getWeather () {
    return weather;
  }

  public CurrentWeather setWeather (
      List<Weather> weather ) {
    this.weather = weather;
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

  public Integer getCod () {
    return cod;
  }

  public CurrentWeather setCod ( Integer cod ) {
    this.cod = cod;
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
    return dt;
  }

  public CurrentWeather setDt ( Date dt ) {
    this.dt = dt;
    return this;
  }

  public Sys getSys () {
    return sys;
  }

  public CurrentWeather setSys ( Sys sys ) {
    this.sys = sys;
    return this;
  }

  public static class Weather {
    private Integer id;
    private String main;
    private String description;
    private String icon;

    public Integer getId () {
      return id;
    }

    public Weather setId ( Integer id ) {
      this.id = id;
      return this;
    }

    public String getMain () {
      return main;
    }

    public Weather setMain ( String main ) {
      this.main = main;
      return this;
    }

    public String getDescription () {
      return description;
    }

    public Weather setDescription ( String description ) {
      this.description = description;
      return this;
    }

    public String getIcon () {
      return icon;
    }

    public Weather setIcon ( String icon ) {
      this.icon = icon;
      return this;
    }
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
    private Integer pressure;
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

    public Integer getPressure () {
      return pressure;
    }

    public Main setPressure ( Integer pressure ) {
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
    private Date sunrise;
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
      return sunrise;
    }

    public Sys setSunrise ( Date sunrise ) {
      this.sunrise = sunrise;
      return this;
    }

    public Date getSunset () {
      return sunset;
    }

    public Sys setSunset ( Date sunset ) {
      this.sunset = sunset;
      return this;
    }
  }
}
