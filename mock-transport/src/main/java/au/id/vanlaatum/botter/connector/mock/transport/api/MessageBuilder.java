package au.id.vanlaatum.botter.connector.mock.transport.api;

import au.id.vanlaatum.botter.api.Transport;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface MessageBuilder {
  MessageBuilder from ( String userId ) throws Transport.UserNotFoundException;

  MessageAssert send () throws InterruptedException, ExecutionException, TimeoutException;

  MessageBuilder channel ( String channelId );
}
