package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.transport.slack.Modal.SlackIM;

public class SlackMessageChannelIM extends AbstractSlackMessageChannel {

  public SlackMessageChannelIM ( SlackIM im ) {
    id = im.getId ();
    name = im.getUser ();
  }

  @Override
  public boolean isDirect () {
    return true;
  }
}
