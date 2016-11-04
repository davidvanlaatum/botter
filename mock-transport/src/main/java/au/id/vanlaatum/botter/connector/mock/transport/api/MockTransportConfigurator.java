package au.id.vanlaatum.botter.connector.mock.transport.api;

public interface MockTransportConfigurator {

  UserBuilder addUser ( String userId );

  ChannelBuilder addChannel ( String id );

  MessageBuilder injectMessage ( String message );

  void start ();

  void stop ();
}
