package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.api.Channel;
import au.id.vanlaatum.botter.transport.slack.modal.SlackTimeStamp;

public abstract class AbstractSlackMessageChannel implements Channel {
  protected String id;
  protected String name;
  private SlackTimeStamp mark;
  private boolean callMark;

  @Override
  public String getID () {
    return id;
  }

  @Override
  public String getName () {
    return name;
  }

  public synchronized SlackTimeStamp getMark () {
    return mark;
  }

  public synchronized AbstractSlackMessageChannel setMark ( SlackTimeStamp mark ) {
    if ( this.mark == null || this.mark.before ( mark ) ) {
      this.mark = mark;
      callMark = true;
    }
    return this;
  }

  public boolean isCallMark () {
    return callMark;
  }

  public synchronized AbstractSlackMessageChannel setCallMark ( boolean callMark ) {
    this.callMark = callMark;
    return this;
  }

  public synchronized void updateMark ( SlackTimeStamp ts ) {
    if ( mark.before ( ts ) ) {
      mark = ts;
      callMark = false;
    }
  }
}
