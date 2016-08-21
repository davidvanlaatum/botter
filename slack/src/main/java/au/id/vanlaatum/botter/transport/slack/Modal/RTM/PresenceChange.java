package au.id.vanlaatum.botter.transport.slack.Modal.RTM;

public class PresenceChange extends BaseEvent {
  private String user;
  private String presence;

  public String getUser () {
    return user;
  }

  public void setUser ( String user ) {
    this.user = user;
  }

  public String getPresence () {
    return presence;
  }

  public void setPresence ( String presence ) {
    this.presence = presence;
  }
}
