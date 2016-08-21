package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.api.Channel;

public abstract class AbstractSlackMessageChannel implements Channel {
  protected String id;
  protected String name;

  public String getID () {
    return id;
  }

  public String getName () {
    return name;
  }
}
