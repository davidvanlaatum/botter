package au.id.vanlaatum.botter.connector.weather.impl;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;

public class WeatherQuestion implements Question {
  private final TimeConstant timeType;
  private final int dow;
  private final Date date;
  private final Subject subject;
  private final String city;
  private final String country;

  public WeatherQuestion ( TimeConstant time, int dow, Date date, Subject subject, String city, String country ) {
    this.timeType = time == null ? TimeConstant.TODAY : time;
    this.dow = dow;
    this.date = ObjectUtils.clone ( date );
    this.subject = subject;
    this.city = city;
    this.country = country;
  }

  @Override
  public QuestionType getType () {
    return QuestionType.WEATHER;
  }

  public TimeConstant getTimeType () {
    return timeType;
  }

  public int getDow () {
    return dow;
  }

  public Date getDate () {
    return ObjectUtils.clone ( date );
  }

  public Subject getSubject () {
    return subject;
  }

  public String getCity () {
    return city;
  }

  public String getCountry () {
    return country;
  }
}
