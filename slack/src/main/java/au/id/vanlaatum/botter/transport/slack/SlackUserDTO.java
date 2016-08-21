package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.api.User;
import au.id.vanlaatum.botter.transport.slack.Modal.SlackUser;

public class SlackUserDTO implements User {
  private final SlackUser user;

  public SlackUserDTO ( SlackUser user ) {
    this.user = user;
  }

  @Override
  public boolean isBot () {
    return user.isBot ();
  }

  @Override
  public boolean isAdmin () {
    return user.isAdmin ();
  }

  @Override
  public boolean isUser () {
    return !user.getDeleted () && !user.isUltraRestricted () && !user.isBot () && !user.isRestricted ();
  }

  @Override
  public String getName () {
    return user.getName ();
  }
}
