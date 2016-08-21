package au.id.vanlaatum.botter.api;

public interface Message {
  String getText ();

  Transport getTransport ();

  User getUser ();

  Channel getChannel ();
}
