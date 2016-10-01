package au.id.vanlaatum.botter.connector.whereis.impl;

import au.id.vanlaatum.botter.api.Channel;
import au.id.vanlaatum.botter.api.Command;
import au.id.vanlaatum.botter.api.Message;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.api.User;
import au.id.vanlaatum.botter.connector.whereis.api.Location;
import au.id.vanlaatum.botter.connector.whereis.api.UserLocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.service.log.LogService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.TimeZone;

import static org.apache.commons.lang3.StringUtils.join;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith ( PowerMockRunner.class )
public class WhereIsKeywordProcessorTest {
  private Command lastCommand;
  private Message lastMessage;
  private Channel lastChannel;
  private User lastUser;
  private Transport lastTransport;

  @Test
  @PrepareForTest ( { WhereIsKeywordProcessor.class, NotInQuestion.class } )
  public void checkForKeywords () throws Exception {
    final Calendar date = new GregorianCalendar ();
    date.setTimeZone ( TimeZone.getTimeZone ( "GMT" ) );
    date.clear ();
    date.set ( 2016, 0, 1 );
    mockStatic ( Calendar.class );
    WhereIsKeywordProcessor processor = new WhereIsKeywordProcessor ();
    UserLocation userLocation = mock ( UserLocation.class );
    when ( userLocation.getCurrentLocationForUser ( eq ( "david@abc123" ), any ( Date.class ) ) ).thenReturn ( null );
    when ( userLocation.getCurrentLocationForUser ( eq ( "james@abc123" ), any ( Date.class ) ) ).thenReturn ( new Location () {
      @Override
      public au.id.vanlaatum.botter.connector.whereis.api.User getUser () {
        return new au.id.vanlaatum.botter.connector.whereis.api.User () {
          @Override
          public String getUniqID () {
            return null;
          }
        };
      }

      @Override
      public String getDescription () {
        return "sick";
      }

      @Override
      public Date getStart () {
        return date.getTime ();
      }

      @Override
      public Date getEnd () {
        return date.getTime ();
      }
    } );
    processor.setUserLocation ( userLocation );
    assertTrue ( processor.checkForKeywords ( mockCommand ( "where is @david" ), null ) );
    verify ( lastCommand ).reply ( "I have no idea where david is" );
    assertTrue ( processor.checkForKeywords ( mockCommand ( "where is @james" ), null ) );
    verify ( lastCommand ).reply ( "james is sick" );
  }

  @Test
  public void updated () throws Exception {

  }

  @SuppressWarnings ( "unchecked" )
  @Test
  @PrepareForTest ( { WhereIsKeywordProcessor.class, NotInQuestion.class } )
  public void parseQuestion () throws Exception {
    WhereIsKeywordProcessor processor = new WhereIsKeywordProcessor ();
    final Calendar date = new GregorianCalendar ();
    date.setTimeZone ( TimeZone.getTimeZone ( "GMT" ) );
    date.clear ();
    date.set ( 2016, 0, 1 );
    mockStatic ( Calendar.class );
    when ( Calendar.getInstance ( (TimeZone) any () ) ).thenAnswer ( new Answer<Calendar> () {
      @Override
      public Calendar answer ( InvocationOnMock invocation ) throws Throwable {
        return (Calendar) date.clone ();
      }
    } );
    final TimeZone GMT = TimeZone.getTimeZone ( "GMT" );

    assertThat ( processor.parseQuestion ( "where is @david", GMT ), allOf (
        isA ( (Class<Question>) WhereIsQuestion.class.asSubclass ( Question.class ) ),
        hasProperty ( "userName", equalTo ( "david" ) )
    ) );
    assertThat ( processor.parseQuestion ( "not in today", GMT ), allOf (
        isA ( (Class<Question>) NotInQuestion.class.asSubclass ( Question.class ) ),
        hasProperty ( "from", equalTo ( date ( "01/01/2016" ) ) ),
        hasProperty ( "to", equalTo ( time ( "01/01/2016 23:59:59" ) ) ),
        hasProperty ( "userName", nullValue () ),
        hasProperty ( "reason", equalTo ( "not in" ) )
    ) );
    assertThat ( processor.parseQuestion ( "not in tomorrow", GMT ), allOf (
        isA ( (Class<Question>) NotInQuestion.class.asSubclass ( Question.class ) ),
        hasProperty ( "from", equalTo ( date ( "02/01/2016" ) ) ),
        hasProperty ( "to", equalTo ( time ( "02/01/2016 23:59:59" ) ) ),
        hasProperty ( "userName", nullValue () ),
        hasProperty ( "reason", equalTo ( "not in" ) )
    ) );
    assertThat ( processor.parseQuestion ( "@david is not in", GMT ), allOf (
        isA ( (Class<Question>) NotInQuestion.class.asSubclass ( Question.class ) ),
        hasProperty ( "from", equalTo ( date ( "01/01/2016" ) ) ),
        hasProperty ( "to", equalTo ( time ( "01/01/2016 23:59:59" ) ) ),
        hasProperty ( "userName", equalTo ( "david" ) ),
        hasProperty ( "reason", equalTo ( "not in" ) )
    ) );
    assertThat ( processor.parseQuestion ( "@david is not in tomorrow", GMT ), allOf (
        isA ( (Class<Question>) NotInQuestion.class.asSubclass ( Question.class ) ),
        hasProperty ( "from", equalTo ( date ( "02/01/2016" ) ) ),
        hasProperty ( "to", equalTo ( time ( "02/01/2016 23:59:59" ) ) ),
        hasProperty ( "userName", equalTo ( "david" ) ),
        hasProperty ( "reason", equalTo ( "not in" ) )
    ) );
    assertThat ( processor.parseQuestion ( "@david is sick", GMT ), allOf (
        isA ( (Class<Question>) NotInQuestion.class.asSubclass ( Question.class ) ),
        hasProperty ( "from", equalTo ( date ( "01/01/2016" ) ) ),
        hasProperty ( "to", equalTo ( time ( "01/01/2016 23:59:59" ) ) ),
        hasProperty ( "userName", equalTo ( "david" ) ),
        hasProperty ( "reason", equalTo ( "sick" ) )
    ) );
  }

  private Date date ( String date ) throws ParseException {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat ( "dd/MM/yyyy" );
    simpleDateFormat.setTimeZone ( TimeZone.getTimeZone ( "GMT" ) );
    return simpleDateFormat.parse ( date );
  }

  private Date time ( String date ) throws ParseException {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat ( "dd/MM/yyyy hh:mm:ss" );
    simpleDateFormat.setTimeZone ( TimeZone.getTimeZone ( "GMT" ) );
    return simpleDateFormat.parse ( date );
  }

  private Command mockCommand ( boolean direct, String... words ) throws Transport.UserNotFoundException {
    lastCommand = mock ( Command.class );
    lastMessage = mock ( Message.class );
    lastChannel = mock ( Channel.class );
    lastUser = mock ( User.class );
    lastTransport = mock ( Transport.class );
    when ( lastCommand.getCommandText () ).thenReturn ( join ( words ) );
    when ( lastCommand.getCommandParts () ).thenReturn ( Arrays.asList ( words ) );
    when ( lastCommand.getMessage () ).thenReturn ( lastMessage );
    when ( lastMessage.getChannel () ).thenReturn ( lastChannel );
    when ( lastChannel.isDirect () ).thenReturn ( direct );
    when ( lastCommand.getUser () ).thenReturn ( lastUser );
    when ( lastUser.getName () ).thenReturn ( "Joe" );
    when ( lastUser.getUniqID () ).thenReturn ( "123" );
    when ( lastCommand.getTransport () ).thenReturn ( lastTransport );
    when ( lastTransport.isMyName ( anyString () ) ).then ( new Answer<Object> () {
      @Override
      public Object answer ( InvocationOnMock invocation ) throws Throwable {
        return invocation.getArguments ()[0].equals ( "bot" );
      }
    } );
    when ( lastTransport.getUser ( anyString () ) ).then ( new Answer<User> () {
      @Override
      public User answer ( final InvocationOnMock invocation ) throws Throwable {
        return new User () {
          @Override
          public boolean isBot () {
            return false;
          }

          @Override
          public boolean isAdmin () {
            return false;
          }

          @Override
          public boolean isUser () {
            return false;
          }

          @Override
          public String getName () {
            return "Joe Blogs";
          }

          @Override
          public String getUniqID () {
            return invocation.getArguments ()[0] + "@abc123";
          }

          @Override
          public TimeZone getTimezone () {
            return TimeZone.getDefault ();
          }
        };
      }
    } );
    return lastCommand;
  }

  private Command mockCommand ( String... words ) throws Transport.UserNotFoundException {
    return mockCommand ( true, words );
  }

  private LogService mockLog () {
    return mock ( LogService.class, new Answer () {
      @Override
      public Object answer ( InvocationOnMock invocation ) throws Throwable {
        if ( Objects.equals ( invocation.getMethod ().getName (), "log" ) ) {
          System.out.println ( Arrays.asList ( invocation.getArguments () ) );
          if ( invocation.getArguments ().length > 2 && invocation.getArguments ()[2] instanceof Exception ) {
            ( (Exception) invocation.getArguments ()[2] ).printStackTrace ();
          }
        }
        return null;
      }
    } );
  }
}
