package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.api.Channel;
import au.id.vanlaatum.botter.api.Message;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.api.User;

public class SlackMessageDTO implements Message {

  private final SlackTransport transport;
  private String text;
  private SlackUserDTO user;
  private AbstractSlackMessageChannel channel;

  SlackMessageDTO ( SlackTransport transport ) {
    this.transport = transport;
  }

  @Override
  public String getText () {
    return text;
  }

  @Override
  public Transport getTransport () {
    return transport;
  }

  @Override
  public User getUser () {
    return user;
  }

  @Override
  public Channel getChannel () {
    return channel;
  }

  public void setText ( String text ) {
    this.text = text;
  }

  public void setUser ( SlackUserDTO user ) {
    this.user = user;
  }

  void setChannel ( AbstractSlackMessageChannel channel ) {
    this.channel = channel;
  }
}
