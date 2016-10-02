package au.id.vanlaatum.botter.connector.whereis.impl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.TimeZone;

import static java.util.Objects.requireNonNull;

public class NotInQuestion implements Question {
  private DateTime from;
  private DateTime to;
  private String userName;
  private String reason;

  NotInQuestion ( QuestionParser.NotinquestionContext notinquestion, TimeZone timezone ) {
    requireNonNull ( notinquestion.from, "from null" );
    userName = notinquestion.userName;
    reason = notinquestion.valueReason;
    switch ( notinquestion.from.getType () ) {
      case DATE:
        from = notinquestion.from.getTime ();
        break;
      case TODAY: {
        final DateTime now = DateTime.now ( DateTimeZone.forTimeZone ( timezone ) );
        from = now.dayOfMonth ().roundFloorCopy ();
        to = from.dayOfMonth ().addToCopy ( 1 );
        break;
      }
      case TOMORROW: {
        final DateTime now = DateTime.now ( DateTimeZone.forTimeZone ( timezone ) );
        from = now.dayOfMonth ().roundFloorCopy ().plusDays ( 1 );
        to = from.dayOfMonth ().addToCopy ( 1 );
        break;
      }
      case NOW:
        from = new DateTime ();
        break;
      case DOW:
        from = DateTime.now ( DateTimeZone.forTimeZone ( timezone ) ).withDayOfWeek ( notinquestion.from.getDow () ).dayOfWeek ()
            .roundFloorCopy ();
        if ( from.isBeforeNow () ) {
          from = from.dayOfYear ().addToCopy ( 7 );
        }
        to = from.dayOfWeek ().addToCopy ( 1 );
        break;
      default:
        throw new RuntimeException ( "Unknown from type " + notinquestion.from.getType () );
    }
    if ( notinquestion.to != null ) {
      switch ( notinquestion.to.getType () ) {
        case DATE:
          to = notinquestion.to.getTime ();
          break;
        case HOUR:
          to = new DateTime ().dayOfMonth ().roundFloorCopy ().hourOfDay ().setCopy ( notinquestion.to.getHour () );
          if ( to.isBeforeNow () ) {
            to = to.hourOfDay ().addToCopy ( 12 );
          }
          break;
        case HOUR_MIN:
          to = new DateTime ().dayOfMonth ().roundFloorCopy ().hourOfDay ().setCopy ( notinquestion.to.getHour () );
          to = to.minuteOfHour ().setCopy ( notinquestion.to.getMinute () );
          if ( to.isBeforeNow () ) {
            to = to.hourOfDay ().addToCopy ( 12 );
          }
          break;
        default:
          throw new RuntimeException ( "Unknown to type " + notinquestion.to.getType () );
      }
    }
  }

  @Override
  public QuestionType getType () {
    return QuestionType.NOTIN;
  }

  public DateTime getFrom () {
    return from;
  }

  public NotInQuestion setFrom ( DateTime from ) {
    this.from = from;
    return this;
  }

  public DateTime getTo () {
    return to;
  }

  public NotInQuestion setTo ( DateTime to ) {
    this.to = to;
    return this;
  }

  public String getUserName () {
    return userName;
  }

  public NotInQuestion setUserName ( String userName ) {
    this.userName = userName;
    return this;
  }

  public String getReason () {
    return reason;
  }

  public NotInQuestion setReason ( String reason ) {
    this.reason = reason;
    return this;
  }
}
