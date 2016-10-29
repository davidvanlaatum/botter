package au.id.vanlaatum.botter.connector.mock.transport.impl;

import au.id.vanlaatum.botter.api.User;

import java.util.TimeZone;

class MockUser implements User {
  private final String userId;

  MockUser ( String userId ) {
    this.userId = userId;
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
    return null;
  }

  @Override
  public String getUniqID () {
    return userId;
  }

  @Override
  public TimeZone getTimezone () {
    return null;
  }
}
