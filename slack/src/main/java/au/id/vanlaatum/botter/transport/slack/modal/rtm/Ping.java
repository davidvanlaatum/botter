package au.id.vanlaatum.botter.transport.slack.modal.rtm;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;

public class Ping extends BaseEvent {
  private Date time = new Date ();

  public Ping () {
    this.setType ( "ping" );
  }

  public Date getTime () {
    return ObjectUtils.clone ( time );
  }
}
