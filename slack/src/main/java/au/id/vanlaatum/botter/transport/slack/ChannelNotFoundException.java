package au.id.vanlaatum.botter.transport.slack;

public class ChannelNotFoundException extends Exception {
  public ChannelNotFoundException ( String msg ) {
    super ( msg );
  }
}
