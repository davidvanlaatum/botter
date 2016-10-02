package au.id.vanlaatum.botter.connector.whereis.impl;

import org.joda.time.DateTime;

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
    from = notinquestion.from.resolveTime ( timezone );
    switch ( notinquestion.from.getType () ) {
      case TODAY:
        to = from.dayOfMonth ().addToCopy ( 1 );
        break;
      case TOMORROW:
        to = from.dayOfMonth ().addToCopy ( 1 );
        break;
      case DOW:
        to = from.dayOfWeek ().addToCopy ( 1 );
        break;
      case DATE:
      case NOW:
        break;
      default:
        throw new RuntimeException ( "Unknown from type " + notinquestion.from.getType () );
    }
    if ( notinquestion.to != null ) {
      to = notinquestion.to.resolveTime ( timezone );
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
