package au.id.vanlaatum.botter.connector.weather.openweather.Model;

import au.id.vanlaatum.botter.connector.weather.api.WeatherDetails;

import java.math.BigDecimal;

public class WeatherDetailsImpl implements WeatherDetails {
  private CurrentWeather currentWeather;

  public WeatherDetailsImpl ( CurrentWeather currentWeather ) {
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
  public Integer getPressure () {
    return currentWeather.getMain ().getPressure ();
  }

  @Override
  public Integer getHumidity () {
    return currentWeather.getMain ().getHumidity ();
  }
}
