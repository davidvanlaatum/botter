package au.id.vanlaatum.botter.transport.slack.modal.rtm;

import au.id.vanlaatum.botter.transport.slack.modal.SlackUser;

public class TeamJoin extends BaseEvent {
  private SlackUser user;

  public SlackUser getUser () {
    return user;
  }

  public void setUser ( SlackUser user ) {
    this.user = user;
  }
}
