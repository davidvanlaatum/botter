package au.id.vanlaatum.botter.connector.weather.openweather.Model;

import au.id.vanlaatum.botter.connector.weather.api.WeatherDetails;

import java.math.BigDecimal;
import java.util.Calendar;

public class WeatherDetailsCurrentImpl implements WeatherDetails {
  private CurrentWeather currentWeather;

  public WeatherDetailsCurrentImpl ( CurrentWeather currentWeather ) {
    this.currentWeather = currentWeather;
  }

  @Override
  public BigDecimal getTemperature () {
    return currentWeather.getMain ().getTemp ();
  }

  @Override
  public BigDecimal getTemperatureMin () {
    return currentWeather.getMain ().getTempMin ();
  }

  @Override
  public BigDecimal getTemperatureMax () {
    return currentWeather.getMain ().getTempMax ();
  }

  @Override
  public BigDecimal getWindSpeed () {
    return currentWeather.getWind ().getSpeed ();
  }

  @Override
  public Integer getWindDirection () {
    return currentWeather.getWind ().getDeg ();
  }

  @Override
  public String getCity () {
    return currentWeather.getName ();
  }

  @Override
  public String getCountry () {
    return currentWeather.getSys ().getCountry ();
  }

  @Override
  public BigDecimal getPressure () {
    return currentWeather.getMain ().getPressure ();
  }

  @Override
  public Integer getHumidity () {
    return currentWeather.getMain ().getHumidity ();
  }

  @Override
  public Calendar getDate () {
    return Calendar.getInstance ();
  }

  @Override
  public boolean isToday () {
    return true;
  }

  @Override
  public String getDescription () {
    return WeatherConditions.toString ( currentWeather.getWeather () );
  }
}
