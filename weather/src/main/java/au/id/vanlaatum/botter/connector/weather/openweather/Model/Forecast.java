package au.id.vanlaatum.botter.connector.weather.openweather.Model;

import au.id.vanlaatum.botter.connector.weather.openweather.UnixTimestampDeserializer;
import au.id.vanlaatum.botter.connector.weather.openweather.UnixTimestampSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Forecast extends BaseResponse {
  private City city;
  private Integer cnt;
  private List<Details> list;

  public List<Details> getList () {
    return list;
  }

  public Forecast setList ( List<Details> list ) {
    this.list = list;
    return this;
  }

  public Integer getCnt () {
    return cnt;
  }

  public Forecast setCnt ( Integer cnt ) {
    this.cnt = cnt;
    return this;
  }

  public City getCity () {
    return city;
  }

  public Forecast setCity ( City city ) {
    this.city = city;
    return this;
  }

  public static class City {
    private Integer id;
    private String name;
    private String country;
    private Integer population;
    private Coord coord;

    public Integer getId () {
      return id;
    }

    public City setId ( Integer id ) {
      this.id = id;
      return this;
    }

    public String getName () {
      return name;
    }

    public City setName ( String name ) {
      this.name = name;
      return this;
    }

    public String getCountry () {
      return country;
    }

    public City setCountry ( String country ) {
      this.country = country;
      return this;
    }

    public Integer getPopulation () {
      return population;
    }

    public City setPopulation ( Integer population ) {
      this.population = population;
      return this;
    }

    public Coord getCoord () {
      return coord;
    }

    public City setCoord ( Coord coord ) {
      this.coord = coord;
      return this;
    }

    public static class Coord {
      private BigDecimal lon;
      private BigDecimal lat;

      public BigDecimal getLat () {
        return lat;
      }

      public Coord setLat ( BigDecimal lat ) {
        this.lat = lat;
        return this;
      }

      public BigDecimal getLon () {
        return lon;
      }

      public Coord setLon ( BigDecimal lon ) {
        this.lon = lon;
        return this;
      }
    }
  }

  public static class Details {
    @JsonDeserialize ( using = UnixTimestampDeserializer.class )
    @JsonSerialize ( using = UnixTimestampSerializer.class )
    private Date dt;
    private Temp temp;
    private BigDecimal pressure;
    private Integer humidity;
    private List<WeatherConditions> weather;
    private BigDecimal speed;
    private Integer deg;
    private Integer clouds;
    private BigDecimal rain;

    public Date getDt () {
      return dt;
    }

    public Details setDt ( Date dt ) {
      this.dt = dt;
      return this;
    }

    public Temp getTemp () {
      return temp;
    }

    public Details setTemp ( Temp temp ) {
      this.temp = temp;
      return this;
    }

    public BigDecimal getPressure () {
      return pressure;
    }

    public Details setPressure ( BigDecimal pressure ) {
      this.pressure = pressure;
      return this;
    }

    public Integer getHumidity () {
      return humidity;
    }

    public Details setHumidity ( Integer humidity ) {
      this.humidity = humidity;
      return this;
    }

    public List<WeatherConditions> getWeather () {
      return weather;
    }

    public Details setWeather ( List<WeatherConditions> weather ) {
      this.weather = weather;
      return this;
    }

    public BigDecimal getSpeed () {
      return speed;
    }

    public Details setSpeed ( BigDecimal speed ) {
      this.speed = speed;
      return this;
    }

    public Integer getDeg () {
      return deg;
    }

    public Details setDeg ( Integer deg ) {
      this.deg = deg;
      return this;
    }

    public Integer getClouds () {
      return clouds;
    }

    public Details setClouds ( Integer clouds ) {
      this.clouds = clouds;
      return this;
    }

    public BigDecimal getRain () {
      return rain;
    }

    public Details setRain ( BigDecimal rain ) {
      this.rain = rain;
      return this;
    }

    public static class Temp {
      private BigDecimal day;
      private BigDecimal min;
      private BigDecimal max;
      private BigDecimal night;
      private BigDecimal eve;
      private BigDecimal morn;

      public BigDecimal getDay () {
        return day;
      }

      public Temp setDay ( BigDecimal day ) {
        this.day = day;
        return this;
      }

      public BigDecimal getMin () {
        return min;
      }

      public Temp setMin ( BigDecimal min ) {
        this.min = min;
        return this;
      }

      public BigDecimal getMax () {
        return max;
      }

      public Temp setMax ( BigDecimal max ) {
        this.max = max;
        return this;
      }

      public BigDecimal getNight () {
        return night;
      }

      public Temp setNight ( BigDecimal night ) {
        this.night = night;
        return this;
      }

      public BigDecimal getEve () {
        return eve;
      }

      public Temp setEve ( BigDecimal eve ) {
        this.eve = eve;
        return this;
      }

      public BigDecimal getMorn () {
        return morn;
      }

      public Temp setMorn ( BigDecimal morn ) {
        this.morn = morn;
        return this;
      }
    }
  }
}
