package au.id.vanlaatum.botter.transport.slack.Modal.RTM;

public class MessageBot extends Message {
  public MessageBot () {
    this.setSubtype ( "bot_message" );
  }
}
