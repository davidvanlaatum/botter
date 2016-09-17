package au.id.vanlaatum.botter.connector.weather.openweather.Model;

import au.id.vanlaatum.botter.connector.weather.api.WeatherDetails;
import au.id.vanlaatum.botter.connector.weather.api.WeatherFetchFailedException;

import java.math.BigDecimal;
import java.util.Calendar;

public class WeatherDetailsForecast implements WeatherDetails {
  private final Forecast weather;
  private final Calendar date;
  private Forecast.Details details;

  public WeatherDetailsForecast ( Forecast weather, Calendar date ) throws WeatherFetchFailedException {
    this.weather = weather;
    this.date = date;
    Calendar end = (Calendar) date.clone ();
    end.roll ( Calendar.DAY_OF_MONTH, true );
    for ( Forecast.Details details : weather.getList () ) {
      if ( details.getDt ().compareTo ( date.getTime () ) >= 0 && details.getDt ().compareTo ( end.getTime () ) < 0 ) {
        this.details = details;
        break;
      }
    }
    if ( details == null ) {
      throw new WeatherFetchFailedException ( "Failed to get details for date " + date );
    }
  }

  @Override
  public BigDecimal getTemperature () {
    return null;
  }

  @Override
  public BigDecimal getTemperatureMin () {
    return details.getTemp ().getMin ();
  }

  @Override
  public BigDecimal getTemperatureMax () {
    return details.getTemp ().getMax ();
  }

  @Override
  public BigDecimal getWindSpeed () {
    return details.getSpeed ();
  }

  @Override
  public Integer getWindDirection () {
    return details.getDeg ();
  }

  @Override
  public String getCity () {
    return weather.getCity ().getName ();
  }

  @Override
  public String getCountry () {
    return weather.getCity ().getCountry ();
  }

  @Override
  public BigDecimal getPressure () {
    return details.getPressure ();
  }

  @Override
  public Integer getHumidity () {
    return details.getHumidity ();
  }

  @Override
  public Calendar getDate () {
    return date;
  }

  @Override
  public boolean isToday () {
    return false;
  }
}
