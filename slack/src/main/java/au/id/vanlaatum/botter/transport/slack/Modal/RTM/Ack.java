package au.id.vanlaatum.botter.transport.slack.Modal.RTM;

import au.id.vanlaatum.botter.transport.slack.Modal.SlackTimeStamp;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Ack extends BasePacket {
  private Boolean ok;
  private SlackTimeStamp ts;

  public SlackTimeStamp getTs () {
    return ts;
  }

  public void setTs ( SlackTimeStamp ts ) {
    this.ts = ts;
  }

  public Boolean getOk () {
    return ok;
  }

  public void setOk ( Boolean ok ) {
    this.ok = ok;
  }

}
