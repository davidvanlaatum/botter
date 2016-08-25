package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.api.Channel;
import au.id.vanlaatum.botter.transport.slack.Modal.SlackTimeStamp;

public abstract class AbstractSlackMessageChannel implements Channel {
  protected String id;
  protected String name;
  private SlackTimeStamp mark;
  private boolean callMark;

  public String getID () {
    return id;
  }

  public String getName () {
    return name;
  }

  public SlackTimeStamp getMark () {
    return mark;
  }

  public synchronized AbstractSlackMessageChannel setMark ( SlackTimeStamp mark ) {
    this.mark = mark;
    callMark = true;
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
