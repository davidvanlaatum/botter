package au.id.vanlaatum.botter.transport.slack.Modal.RTM;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageChanged extends Message {
  private Message message;
  private Message previousMessage;

  public Message getMessage () {
    return message;
  }

  public MessageChanged setMessage ( Message message ) {
    this.message = message;
    return this;
  }

  @JsonProperty ( "previous_message" )
  public Message getPreviousMessage () {
    return previousMessage;
  }

  public MessageChanged setPreviousMessage ( Message previousMessage ) {
    this.previousMessage = previousMessage;
    return this;
  }
}
