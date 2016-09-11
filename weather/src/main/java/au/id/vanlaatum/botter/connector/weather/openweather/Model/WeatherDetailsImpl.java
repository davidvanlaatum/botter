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
  public String getCity () {
    return currentWeather.getName();
  }

  @Override
  public String getCountry () {
    return currentWeather.getSys ().getCountry ();
  }
}
