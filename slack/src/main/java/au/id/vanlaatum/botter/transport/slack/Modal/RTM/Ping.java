package au.id.vanlaatum.botter.transport.slack.Modal.RTM;

import java.util.Date;

public class Ping extends BaseEvent {
  private Date time = new Date ();

  public Ping () {
    this.setType ( "ping" );
  }

  public Date getTime () {
    return time;
  }
}
