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
  private au.id.vanlaatum.botter.transport.slack.modal.rtm.Message originalMessage;

  SlackMessageDTO ( SlackTransport transport ) {
    this.transport = transport;
  }

  public au.id.vanlaatum.botter.transport.slack.modal.rtm.Message getOriginalMessage () {
    return originalMessage;
  }

  public SlackMessageDTO setOriginalMessage (
      au.id.vanlaatum.botter.transport.slack.modal.rtm.Message originalMessage ) {
    this.originalMessage = originalMessage;
    return this;
  }

  @Override
  public String getText () {
    return text;
  }

  public void setText ( String text ) {
    this.text = text;
  }

  @Override
  public Transport getTransport () {
    return transport;
  }

  @Override
  public User getUser () {
    return user;
  }

  public void setUser ( SlackUserDTO user ) {
    this.user = user;
  }

  @Override
  public Channel getChannel () {
    return channel;
  }

  void setChannel ( AbstractSlackMessageChannel channel ) {
    this.channel = channel;
  }
}
