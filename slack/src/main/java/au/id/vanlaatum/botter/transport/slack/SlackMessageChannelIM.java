package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.transport.slack.Modal.SlackIM;

public class SlackMessageChannelIM extends AbstractSlackMessageChannel {
  private SlackIM im;

  public SlackMessageChannelIM ( SlackIM im ) {
    this.im = im;
    id = im.getId ();
    name = im.getUser ();
  }

  @Override
  public boolean isDirect () {
    return true;
  }
}
