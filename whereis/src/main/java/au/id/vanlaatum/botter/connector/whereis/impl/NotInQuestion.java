package au.id.vanlaatum.botter.connector.whereis.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static java.util.Objects.requireNonNull;

public class NotInQuestion implements Question {
  private Date from;
  private Date to;
  private String userName;
  private String reason;

  NotInQuestion ( QuestionParser.NotinquestionContext notinquestion, TimeZone timezone ) {
    requireNonNull ( notinquestion.fromType, "fromType null" );
    userName = notinquestion.userName;
    reason = notinquestion.valueReason;
    switch ( notinquestion.fromType ) {
      case DATE:
        from = notinquestion.from;
        break;
      case TODAY: {
        final Calendar now = Calendar.getInstance ( timezone );
        now.set ( Calendar.HOUR_OF_DAY, 0 );
        now.clear ( Calendar.MINUTE );
        now.clear ( Calendar.SECOND );
        now.clear ( Calendar.MILLISECOND );
        from = now.getTime ();
        now.set ( Calendar.HOUR_OF_DAY, 23 );
        now.set ( Calendar.MINUTE, 59 );
        now.set ( Calendar.SECOND, 59 );
        to = now.getTime ();
        break;
      }
      case TOMORROW: {
        final Calendar now = Calendar.getInstance ( timezone );
        now.roll ( Calendar.DAY_OF_MONTH, true );
        now.set ( Calendar.HOUR_OF_DAY, 0 );
        now.clear ( Calendar.MINUTE );
        now.clear ( Calendar.SECOND );
        now.clear ( Calendar.MILLISECOND );
        from = now.getTime ();
        now.set ( Calendar.HOUR_OF_DAY, 23 );
        now.set ( Calendar.MINUTE, 59 );
        now.set ( Calendar.SECOND, 59 );
        to = now.getTime ();
        break;
      }
      case DOW:
        break;
    }
  }

  @Override
  public QuestionType getType () {
    return QuestionType.NOTIN;
  }

  public Date getFrom () {
    return from;
  }

  public NotInQuestion setFrom ( Date from ) {
    this.from = from;
    return this;
  }

  public Date getTo () {
    return to;
  }

  public NotInQuestion setTo ( Date to ) {
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
