package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.transport.slack.modal.SlackChannel;

public class SlackMessageChannel extends AbstractSlackMessageChannel {

  public SlackMessageChannel ( SlackChannel channel ) {
    id = channel.getId ();
    name = channel.getName ();
  }

  @Override
  public boolean isDirect () {
    return false;
  }
}
