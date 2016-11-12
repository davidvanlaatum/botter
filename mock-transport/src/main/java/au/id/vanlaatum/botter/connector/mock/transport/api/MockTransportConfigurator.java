package au.id.vanlaatum.botter.connector.mock.transport.api;

import au.id.vanlaatum.botter.api.BotFactory;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.connector.mock.transport.impl.MockChannel;
import au.id.vanlaatum.botter.connector.mock.transport.impl.MockUser;

public interface MockTransportConfigurator {

  UserBuilder addUser ( String userId );

  ChannelBuilder addChannel ( String id );

  MessageBuilder injectMessage ( String message );

  void start ();

  void stop ();

  BotFactory getBotFactory ();

  Transport getTransport ();

  MockUser getUserById ( String userId ) throws Transport.UserNotFoundException;

  MockChannel getChannel ( String channelId );
}
