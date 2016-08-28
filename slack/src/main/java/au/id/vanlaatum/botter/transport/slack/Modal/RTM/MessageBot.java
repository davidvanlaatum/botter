package au.id.vanlaatum.botter.transport.slack.Modal.RTM;

import java.util.Map;

public class MessageBot extends Message {
  public MessageBot () {
    this.setSubtype ( "bot_message" );
  }
}
