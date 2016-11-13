package au.id.vanlaatum.botter.transport.slack.modal.rtm;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;

public class Pong extends BaseEvent {
  private Date time;

  public Date getTime () {
    return ObjectUtils.clone ( time );
  }

  public void setTime ( Date time ) {
    this.time = ObjectUtils.clone ( time );
  }
}
