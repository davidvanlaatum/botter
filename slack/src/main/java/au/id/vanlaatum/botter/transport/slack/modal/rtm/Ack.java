package au.id.vanlaatum.botter.transport.slack.modal.rtm;

public class Ack extends BasePacket {
  private Boolean ok;

  public Boolean getOk () {
    return ok;
  }

  public void setOk ( Boolean ok ) {
    this.ok = ok;
  }

}
