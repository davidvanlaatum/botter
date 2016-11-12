package au.id.vanlaatum.botter.connector.mock.transport.impl;

import au.id.vanlaatum.botter.api.Channel;
import au.id.vanlaatum.botter.api.Message;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.api.User;
import au.id.vanlaatum.botter.connector.mock.transport.api.MessageAssert;
import au.id.vanlaatum.botter.connector.mock.transport.api.MockTransportConfigurator;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

@SuppressWarnings ( "WeakerAccess" )
public class MessageAssertImpl implements MessageAssert, Message {
  private final MockTransportConfigurator mockTransportConfigurator;
  private final String message;
  private final MockUser user;
  private final MockChannel channel;
  private final List<MessageResponse> responsesList = new ArrayList<> ();

  MessageAssertImpl ( MockTransportConfigurator mockTransportConfigurator, String message, MockUser user, MockChannel channel ) {
    this.mockTransportConfigurator = requireNonNull ( mockTransportConfigurator );
    this.message = requireNonNull ( message );
    this.user = requireNonNull ( user );
    this.channel = requireNonNull ( channel );
  }

  @Override
  public String getText () {
    return message;
  }

  @Override
  public Transport getTransport () {
    return mockTransportConfigurator.getTransport ();
  }

  @Override
  public User getUser () {
    return user;
  }

  @Override
  public Channel getChannel () {
    return channel;
  }

  void reply ( String text ) {
    responsesList.add ( new MessageResponse ( MessageResponseType.REPLY, text ) );
  }

  void error ( String text ) {
    responsesList.add ( new MessageResponse ( MessageResponseType.ERROR, text ) );
  }

  void annotate ( String text ) {
    responsesList.add ( new MessageResponse ( MessageResponseType.ANNOTATE, text ) );
  }

  @Override
  public void assertMessage ( MessageResponseType type, Matcher<String> message ) {
    MatcherAssert.assertThat ( responsesList, CoreMatchers.<MessageResponse>hasItem ( CoreMatchers.allOf (
        hasProperty ( "message", message ),
        hasProperty ( "type", equalTo ( type ) )
    ) ) );
  }

  @Override
  public void assertMessage ( MessageResponseType type, String message ) {
    assertMessage ( type, equalTo ( message ) );
  }

  public static class MessageResponse {
    MessageResponseType type;
    String message;

    MessageResponse ( MessageResponseType type, String message ) {
      this.type = type;
      this.message = message;
    }

    public MessageResponseType getType () {
      return type;
    }

    public String getMessage () {
      return message;
    }
  }
}
