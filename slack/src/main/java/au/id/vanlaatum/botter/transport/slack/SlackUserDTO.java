package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.api.User;
import au.id.vanlaatum.botter.transport.slack.modal.SlackUser;
import org.apache.commons.lang3.StringUtils;

import java.util.TimeZone;

public class SlackUserDTO implements User {
  private final SlackUser user;
  private final String uniqID;

  public SlackUserDTO ( SlackUser user, SlackTransport transport ) {
    this.user = user;
    uniqID = transport.getPID () + "." + user.getId ();
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

  @Override
  public String getUniqID () {
    return uniqID;
  }

  @Override
  public TimeZone getTimezone () {
    return StringUtils.isNoneBlank ( user.getTz () ) ? TimeZone.getTimeZone ( user.getTz () ) : TimeZone.getDefault ();
  }
}
