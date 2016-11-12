package au.id.vanlaatum.botter.connector.mock.transport.impl;

import au.id.vanlaatum.botter.api.Channel;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.api.User;
import au.id.vanlaatum.botter.connector.mock.transport.api.MessageAssert;
import au.id.vanlaatum.botter.connector.mock.transport.api.MessageBuilder;
import au.id.vanlaatum.botter.connector.mock.transport.api.MockTransportConfigurator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class MessageBuilderImpl implements MessageBuilder {
  public static final int TIMEOUT = 10;
  private final MockTransportConfigurator mockTransportConfigurator;
  private String message;
  private User user;
  private Channel channel;

  MessageBuilderImpl ( MockTransportConfigurator mockTransportConfigurator, String message ) {
    this.mockTransportConfigurator = mockTransportConfigurator;
    this.message = message;
  }

  @Override
  public MessageBuilder from ( String userId ) throws Transport.UserNotFoundException {
    this.user = mockTransportConfigurator.getUserById ( userId );
    return this;
  }

  @Override
  public MessageAssert send () throws InterruptedException, ExecutionException, TimeoutException {
    final MessageAssertImpl messageAssert = new MessageAssertImpl ( mockTransportConfigurator, message, user, channel );
    mockTransportConfigurator.getBotFactory ().processMessage ( messageAssert ).get ( TIMEOUT, TimeUnit.SECONDS );
    return messageAssert;
  }

  @Override
  public MessageBuilder channel ( String channelId ) {
    channel = mockTransportConfigurator.getChannel ( channelId );
    return this;
  }
}
