package au.id.vanlaatum.botter.connector.weather.openweather.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseResponse {
  private Integer cod;
  private String message;

  public Integer getCod () {
    return cod;
  }

  public BaseResponse setCod ( Integer cod ) {
    this.cod = cod;
    return this;
  }

  public String getMessage () {
    return message;
  }

  public BaseResponse setMessage ( String message ) {
    this.message = message;
    return this;
  }

  @JsonIgnore
  public boolean isSuccess () {
    return cod == 200;
  }
}
