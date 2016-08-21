package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.transport.slack.Modal.SlackChannel;

public class SlackMessageChannel extends AbstractSlackMessageChannel {
  private SlackChannel channel;

  public SlackMessageChannel ( SlackChannel channel ) {
    id = channel.getId ();
    name = channel.getName ();
    this.channel = channel;
  }

  @Override
  public boolean isDirect () {
    return false;
  }
}
