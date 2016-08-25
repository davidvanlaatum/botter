package au.id.vanlaatum.botter.transport.slack.Modal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;

import java.util.Objects;

import static java.lang.String.format;


@SuppressWarnings ( "WeakerAccess" )
@JsonDeserialize ( converter = SlackTimeStamp.TSConverter.class )
public class SlackTimeStamp {
  private long seconds;
  private int miliseconds;

  public SlackTimeStamp ( String ts ) {
    final int i = ts.indexOf ( "." );
    seconds = Long.parseLong ( ts.substring ( 0, i ) );
    miliseconds = Integer.parseInt ( ts.substring ( i + 1 ) );
  }

  public SlackTimeStamp () {
  }

  public long getSeconds () {
    return seconds;
  }

  public void setSeconds ( long seconds ) {
    this.seconds = seconds;
  }

  public int getMiliseconds () {
    return miliseconds;
  }

  public void setMiliseconds ( int miliseconds ) {
    this.miliseconds = miliseconds;
  }

  @Override
  public String toString () {
    return format ( "%d.%06d", seconds, miliseconds );
  }

  @Override
  public boolean equals ( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass () != o.getClass () ) {
      return false;
    }
    SlackTimeStamp that = (SlackTimeStamp) o;
    return seconds == that.seconds &&
        miliseconds == that.miliseconds;
  }

  @Override
  public int hashCode () {
    return Objects.hash ( seconds, miliseconds );
  }

  public boolean before ( SlackTimeStamp ts ) {
    return seconds < ts.seconds || ( seconds == ts.seconds && miliseconds < ts.miliseconds );
  }

  public static class TSConverter extends StdConverter<String, SlackTimeStamp> {
    @Override
    public SlackTimeStamp convert ( String s ) {
      return new SlackTimeStamp ( s );
    }
  }
}
