package au.id.vanlaatum.botter.transport.slack.Modal.RTM;

import java.util.Date;

public class Pong extends BaseEvent {
  private Date time;

  public Date getTime () {
    return time;
  }

  public void setTime ( Date time ) {
    this.time = time;
  }
}
