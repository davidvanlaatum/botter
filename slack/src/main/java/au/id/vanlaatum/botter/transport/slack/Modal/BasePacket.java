package au.id.vanlaatum.botter.transport.slack.Modal;

public class BasePacket {
  private Boolean ok;
  private String error;

  public Boolean getOk () {
    return ok;
  }

  public void setOk ( Boolean ok ) {
    this.ok = ok;
  }

  public String getError () {
    return error;
  }

  public BasePacket setError ( String error ) {
    this.error = error;
    return this;
  }
}
