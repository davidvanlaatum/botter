package au.id.vanlaatum.botter.core.test;

import au.id.vanlaatum.botter.api.User;

import java.util.TimeZone;

public class MockUser implements User {
  private String userName;

  public MockUser ( String userName ) {
    this.userName = userName;
  }

  @Override
  public boolean isBot () {
    return false;
  }

  @Override
  public boolean isAdmin () {
    return false;
  }

  @Override
  public boolean isUser () {
    return false;
  }

  @Override
  public String getName () {
    return userName;
  }

  @Override
  public String getUniqID () {
    return userName + "@abc123";
  }

  @Override
  public TimeZone getTimezone () {
    return TimeZone.getTimeZone ( "GMT" );
  }
}
