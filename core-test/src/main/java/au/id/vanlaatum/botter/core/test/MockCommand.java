package au.id.vanlaatum.botter.core.test;

import au.id.vanlaatum.botter.api.KeyWordProcessor;
import au.id.vanlaatum.botter.core.CommandImpl;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

@SuppressWarnings ( "WeakerAccess" )
public class MockCommand extends CommandImpl {
  private final List<MessageCall> recordedCalls = new ArrayList<> ();
  private final MockTransport transport = new MockTransport ();
  private final MockUser user = new MockUser ( "joe" );

  public MockCommand ( String message ) {
    setCommandText ( message );
    setTransport ( transport );
    setUser ( user );
    addUser ( "joe" );
  }

  @Override
  public void reply ( String message ) {
    recordedCalls.add ( new MessageCall ( CallType.REPLY, message ) );
  }

  @Override
  public void error ( String message ) {
    recordedCalls.add ( new MessageCall ( CallType.ERROR, message ) );
  }

  @Override
  public void error ( Throwable e ) {
    super.error ( e );
  }

  @Override
  public void annotate ( String message ) {
    recordedCalls.add ( new MessageCall ( CallType.ANNOTATE, message ) );
  }

  public MockCommand verifyMessageCall ( CallType callType, Matcher<String> message ) {
    assertThat ( recordedCalls, CoreMatchers.<MessageCall>hasItem ( allOf (
        hasProperty ( "callType", equalTo ( callType ) ),
        hasProperty ( "message", message )
    ) ) );
    return this;
  }

  public MockCommand verifyMessageCall ( CallType callType, String message ) {
    return verifyMessageCall ( callType, equalTo ( message ) );
  }

  public MockCommand addUser ( String id ) {
    transport.addUser ( id );
    return this;
  }

  public MockCommand assertMatchesKeywords ( KeyWordProcessor processor ) {
    Assert.assertTrue ( processor.checkForKeywords ( this, null ) );
    return this;
  }

  public enum CallType {
    REPLY,
    ERROR,
    ANNOTATE
  }

  public class MessageCall {
    CallType callType;
    String message;

    MessageCall ( CallType callType, String message ) {
      this.callType = callType;
      this.message = message;
    }

    public CallType getCallType () {
      return callType;
    }

    public String getMessage () {
      return message;
    }
  }
}
