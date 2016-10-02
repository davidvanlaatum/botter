package au.id.vanlaatum.botter.connector.whereis.impl;

import org.joda.time.DateTime;

public class RelativeTime {
  private final TimeConstant type;
  private DateTime time;
  private int dow;
  private int hour;
  private int minute;

  public RelativeTime ( DateTime time ) {
    type = TimeConstant.DATE;
    this.time = time;
  }

  public RelativeTime ( TimeConstant type ) {
    this.type = type;
  }

  public RelativeTime ( int dow ) {
    type = TimeConstant.DOW;
    this.dow = dow;
  }

  public RelativeTime ( int hour, int minute ) {
    type = TimeConstant.HOUR_MIN;
    this.hour = hour;
    this.minute = minute;
  }

  public DateTime getTime () {
    return time;
  }

  public TimeConstant getType () {
    return type;
  }

  public int getDow () {
    return dow;
  }

  public int getHour () {
    return hour;
  }

  public int getMinute () {
    return minute;
  }
}
