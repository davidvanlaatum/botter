package au.id.vanlaatum.botter.transport.slack.Modal.RTM;


import java.net.URI;

public class ReconnectURL extends BaseEvent {
  private URI url;

  public URI getUrl () {
    return url;
  }

  public void setUrl ( URI url ) {
    this.url = url;
  }
}
