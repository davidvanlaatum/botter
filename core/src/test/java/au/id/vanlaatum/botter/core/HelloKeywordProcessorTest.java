package au.id.vanlaatum.botter.core;

import au.id.vanlaatum.botter.api.Channel;
import au.id.vanlaatum.botter.api.Command;
import au.id.vanlaatum.botter.api.Message;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.api.User;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class HelloKeywordProcessorTest {

  private Command lastCommand;
  private Message lastMessage;
  private Channel lastChannel;
  private User lastUser;
  private Transport lastTransport;

  @Test
  public void checkForKeywords () throws Exception {
    HelloKeywordProcessor processor = new HelloKeywordProcessor ();
    processor.setBotFactory ( new BotFactoryImpl () );
    processor.setRandom ( new Random () {
      @Override
      public int nextInt ( int bound ) {
        return 0;
      }
    } );

    assertFalse ( processor.checkForKeywords ( mockCommand (), Collections.<Boolean>emptyList () ) );
    assertTrue ( processor.checkForKeywords ( mockCommand ( "hello" ), Collections.<Boolean>emptyList () ) );
    verify ( lastCommand ).reply ( "Hello" );
    assertTrue ( processor.checkForKeywords ( mockCommand ( "hi" ), Collections.<Boolean>emptyList () ) );
    verify ( lastCommand ).reply ( "Hello" );
    verify ( lastCommand, Mockito.atLeast ( 0 ) ).getCommandParts ();
    verify ( lastCommand, Mockito.atLeast ( 0 ) ).getMessage ();
    verifyNoMoreInteractions ( lastCommand );
    assertTrue (
        processor.checkForKeywords ( mockCommand ( false, "good", "evening" ), Collections.<Boolean>emptyList () ) );
    verify ( lastCommand ).reply ( Mockito.eq ( "Hello Joe" ) );
    assertTrue (
        processor.checkForKeywords ( mockCommand ( false, "hi", "bot" ), Collections.<Boolean>emptyList () ) );
    verify ( lastCommand ).reply ( Mockito.eq ( "Hello Joe" ) );
    assertTrue (
        processor.checkForKeywords ( mockCommand ( false, "hi", "all" ), Collections.<Boolean>emptyList () ) );
    verify ( lastCommand ).reply ( Mockito.eq ( "Hello Joe" ) );
    assertFalse (
        processor.checkForKeywords ( mockCommand ( false, "hi", "jill" ), Collections.<Boolean>emptyList () ) );
    assertTrue (
        processor.checkForKeywords ( mockCommand ( false, "bye", "all" ), Collections.<Boolean>emptyList () ) );
    verify ( lastCommand ).reply ( Mockito.eq ( "bye Joe" ) );
    assertTrue ( processor.checkForKeywords ( mockCommand ( "bye" ), Collections.<Boolean>emptyList () ) );
    verify ( lastCommand ).reply ( "bye" );
  }

  private Command mockCommand ( boolean direct, String... words ) {
    lastCommand = mock ( Command.class );
    lastMessage = mock ( Message.class );
    lastChannel = mock ( Channel.class );
    lastUser = mock ( User.class );
    lastTransport = mock ( Transport.class );
    when ( lastCommand.getCommandParts () ).thenReturn ( Arrays.asList ( words ) );
    when ( lastCommand.getMessage () ).thenReturn ( lastMessage );
    when ( lastMessage.getChannel () ).thenReturn ( lastChannel );
    when ( lastChannel.isDirect () ).thenReturn ( direct );
    when ( lastCommand.getUser () ).thenReturn ( lastUser );
    when ( lastUser.getName () ).thenReturn ( "Joe" );
    when ( lastCommand.getTransport () ).thenReturn ( lastTransport );
    when ( lastTransport.isMyName ( anyString () ) ).then ( new Answer<Object> () {
      @Override
      public Object answer ( InvocationOnMock invocation ) throws Throwable {
        return invocation.getArguments ()[0].equals ( "bot" );
      }
    } );
    return lastCommand;
  }

  private Command mockCommand ( String... words ) {
    return mockCommand ( true, words );
  }
}
