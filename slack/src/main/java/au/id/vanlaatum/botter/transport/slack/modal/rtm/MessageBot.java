package au.id.vanlaatum.botter.transport.slack.modal.rtm;

public class MessageBot extends Message {
  public MessageBot () {
    this.setSubtype ( "bot_message" );
  }
}
