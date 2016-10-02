package au.id.vanlaatum.botter.connector.whereis.impl;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.TimeZone;

public class RelativeTime {
  private final TimeConstant type;
  private DateTime time;
  private int dow;
  private int hour;
  private int minute;
  private boolean hasHalfDay;

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

  public RelativeTime ( int hour, int minute, String ampm ) {
    type = TimeConstant.HOUR_MIN;
    this.hour = hour;
    this.minute = minute;
    hasHalfDay = StringUtils.isNoneBlank ( ampm );
    if ( "pm".equalsIgnoreCase ( ampm ) ) {
      this.hour += 12;
    }
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

  public DateTime resolveTime ( TimeZone timezone ) {
    DateTime rt;
    switch ( type ) {
      case DATE:
        rt = time;
        break;
      case TODAY:
        rt = DateTime.now ( DateTimeZone.forTimeZone ( timezone ) ).dayOfMonth ().roundFloorCopy ();
        break;
      case TOMORROW:
        rt = DateTime.now ( DateTimeZone.forTimeZone ( timezone ) ).dayOfMonth ().roundFloorCopy ().plusDays ( 1 );
        break;
      case DOW:
        rt = DateTime.now ( DateTimeZone.forTimeZone ( timezone ) ).withDayOfWeek ( dow ).dayOfWeek ()
            .roundFloorCopy ();
        if ( rt.isBeforeNow () ) {
          rt = rt.dayOfYear ().addToCopy ( 7 );
        }
        break;
      case NOW:
        rt = DateTime.now ( DateTimeZone.forTimeZone ( timezone ) );
        break;
      case HOUR_MIN:
        rt = DateTime.now ( DateTimeZone.forTimeZone ( timezone ) ).dayOfMonth ().roundFloorCopy ().hourOfDay ().setCopy ( hour )
            .minuteOfHour ().setCopy ( minute );
        if ( rt.isBeforeNow () && !hasHalfDay ) {
          rt = rt.hourOfDay ().addToCopy ( 12 );
        }
        break;
      default:
        throw new UnsupportedOperationException ( type.toString () );
    }
    return rt;
  }
}
