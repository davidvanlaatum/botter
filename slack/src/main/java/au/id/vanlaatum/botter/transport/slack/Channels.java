package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.transport.slack.Modal.SlackChannel;
import au.id.vanlaatum.botter.transport.slack.Modal.SlackIM;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class Channels {
  private final Map<String, AbstractSlackMessageChannel> channels = new TreeMap<> ();

  public void addChannel ( SlackChannel channel ) {
    SlackMessageChannel c = new SlackMessageChannel ( channel );
    channels.put ( channel.getId (), c );
  }

  public void addChannel ( SlackIM im ) {
    SlackMessageChannelIM c = new SlackMessageChannelIM ( im );
    channels.put ( im.getId (), c );
  }

  public AbstractSlackMessageChannel get ( String id ) throws ChannelNotFoundException {
    final AbstractSlackMessageChannel rt = channels.get ( id );
    if ( rt == null ) {
      throw new ChannelNotFoundException ( "Channel with id " + id + " not found" );
    }
    return rt;
  }

  public int size () {
    return channels.size ();
  }

  public Collection<AbstractSlackMessageChannel> all () {
    return Collections.unmodifiableCollection ( channels.values () );
  }
}
