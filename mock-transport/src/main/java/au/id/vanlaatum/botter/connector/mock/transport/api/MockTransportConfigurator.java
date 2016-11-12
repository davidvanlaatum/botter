package au.id.vanlaatum.botter.connector.mock.transport.api;

import au.id.vanlaatum.botter.api.Transport;

public interface MockTransportConfigurator {

  UserBuilder addUser ( String userId );

  ChannelBuilder addChannel ( String id );

  MessageBuilder injectMessage ( String message );

  void start ();

  void stop ();

  Transport getTransport ();
}
