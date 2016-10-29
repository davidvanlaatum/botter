package au.id.vanlaatum.botter.connector.mock.transport.impl;

import au.id.vanlaatum.botter.api.Channel;

class MockChannel implements Channel {
  private boolean direct;
  private String name;
  private String id;

  public MockChannel ( boolean direct, String name, String id ) {
    this.direct = direct;
    this.name = name;
    this.id = id;
  }

  @Override
  public boolean isDirect () {
    return direct;
  }

  @Override
  public String getName () {
    return name;
  }

  @Override
  public String getID () {
    return id;
  }
}
