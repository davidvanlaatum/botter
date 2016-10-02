package au.id.vanlaatum.botter.connector.whereis.impl;

import au.id.vanlaatum.botter.api.Channel;
import au.id.vanlaatum.botter.api.Command;
import au.id.vanlaatum.botter.api.Message;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.api.User;
import au.id.vanlaatum.botter.connector.whereis.api.Location;
import au.id.vanlaatum.botter.connector.whereis.api.UserLocation;
import au.id.vanlaatum.botter.core.test.MockCommand;
import au.id.vanlaatum.botter.core.test.MockLogService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import static org.apache.commons.lang3.StringUtils.join;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith ( PowerMockRunner.class )
public class WhereIsKeywordProcessorTest {
  private Command lastCommand;
  private Message lastMessage;
  private Channel lastChannel;
  private User lastUser;
  private Transport lastTransport;

  @Before
  public void setup () {
    DateTimeUtils.setCurrentMillisFixed ( date ( "2016-01-01 00:00:00GMT" ).getMillis () );
  }

  @After
  public void tearDown () {
    DateTimeUtils.setCurrentMillisSystem ();
  }

  @Test
  @PrepareForTest ( { WhereIsKeywordProcessor.class, NotInQuestion.class } )
  public void checkForKeywords () throws Exception {
    final DateTime now = date ( "2016-01-01 00:00:00GMT" );
    WhereIsKeywordProcessor processor = new WhereIsKeywordProcessor ();
    UserLocation userLocation = mock ( UserLocation.class );
    when ( userLocation.getCurrentLocationForUser ( eq ( "david@abc123" ), any ( Date.class ) ) ).thenReturn ( null );
    when ( userLocation.getCurrentLocationForUser ( eq ( "james@abc123" ), any ( Date.class ) ) ).thenReturn ( new Location () {
      @Override
      public au.id.vanlaatum.botter.connector.whereis.api.User getUser () {
        return new au.id.vanlaatum.botter.connector.whereis.api.User () {
          @Override
          public String getUniqID () {
            return "james@abc123";
          }
        };
      }

      @Override
      public String getDescription () {
        return "sick";
      }

      @Override
      public DateTime getStart () {
        return now;
      }

      @Override
      public DateTime getEnd () {
        return now.dayOfMonth ().addToCopy ( 1 );
      }
    } );
    when ( userLocation.addLocationForUser ( anyString (), any ( DateTime.class ), any ( DateTime.class ), anyString () ) ).thenAnswer (
        new Answer<Location> () {
          @Override
          public Location answer ( final InvocationOnMock invocation ) throws Throwable {
            return new Location () {
              @Override
              public au.id.vanlaatum.botter.connector.whereis.api.User getUser () {
                return null;
              }

              @Override
              public String getDescription () {
                return (String) invocation.getArguments ()[3];
              }

              @Override
              public DateTime getStart () {
                return (DateTime) invocation.getArguments ()[1];
              }

              @Override
              public DateTime getEnd () {
                return (DateTime) invocation.getArguments ()[2];
              }
            };
          }
        } );
    processor.setUserLocation ( userLocation );
    processor.setLog ( new MockLogService () );
    new MockCommand ( "where is @david" ).addUser ( "david" ).assertMatchesKeywords ( processor )
        .verifyMessageCall ( MockCommand.CallType.REPLY, "I have no idea where david is" );
    new MockCommand ( "where is @james" ).addUser ( "james" ).assertMatchesKeywords ( processor )
        .verifyMessageCall ( MockCommand.CallType.REPLY, "james is sick" );
    new MockCommand ( "not in today" ).assertMatchesKeywords ( processor )
        .verifyMessageCall ( MockCommand.CallType.REPLY, "ok I have set your location to not in between 01-01-2016 and 02-01-2016" );
    new MockCommand ( "not in tomorrow" ).assertMatchesKeywords ( processor )
        .verifyMessageCall ( MockCommand.CallType.REPLY, "ok I have set your location to not in between 02-01-2016 and 03-01-2016" );
    new MockCommand ( "not in sunday" ).assertMatchesKeywords ( processor )
        .verifyMessageCall ( MockCommand.CallType.REPLY, "ok I have set your location to not in between 03-01-2016 and 04-01-2016" );
  }

  @Test
  @PrepareForTest ( { WhereIsKeywordProcessor.class, DateTime.class } )
  public void getWhereIsReply () throws Exception {
    WhereIsKeywordProcessor processor = new WhereIsKeywordProcessor ();
    processor.setLog ( new MockLogService () );
    final MockCommand command = new MockCommand ( "where is @joe" );
    assertEquals ( "joe is sick", processor.getWhereIsReply ( new WhereIsQuestion ( "joe" ), command, new Location () {
      @Override
      public au.id.vanlaatum.botter.connector.whereis.api.User getUser () {
        return new au.id.vanlaatum.botter.connector.whereis.api.User () {
          @Override
          public String getUniqID () {
            return command.getUser ().getUniqID ();
          }
        };
      }

      @Override
      public String getDescription () {
        return "sick";
      }

      @Override
      public DateTime getStart () {
        return DateTime.parse ( "2016-01-01" );
      }

      @Override
      public DateTime getEnd () {
        return DateTime.parse ( "2016-01-02" );
      }
    } ) );
    assertEquals ( "joe is sick until 03-01-2016", processor.getWhereIsReply ( new WhereIsQuestion ( "joe" ), command, new Location () {
      @Override
      public au.id.vanlaatum.botter.connector.whereis.api.User getUser () {
        return new au.id.vanlaatum.botter.connector.whereis.api.User () {
          @Override
          public String getUniqID () {
            return command.getUser ().getUniqID ();
          }
        };
      }

      @Override
      public String getDescription () {
        return "sick";
      }

      @Override
      public DateTime getStart () {
        return date ( "2016-01-01 00:00:00GMT" );
      }

      @Override
      public DateTime getEnd () {
        return date ( "2016-01-03 00:00:00GMT" );
      }
    } ) );
    assertEquals ( "joe is sick until 12:00PM 03-01-2016+0000",
        processor.getWhereIsReply ( new WhereIsQuestion ( "joe" ), command, new Location () {
          @Override
          public au.id.vanlaatum.botter.connector.whereis.api.User getUser () {
            return new au.id.vanlaatum.botter.connector.whereis.api.User () {
              @Override
              public String getUniqID () {
                return command.getUser ().getUniqID ();
              }
            };
          }

          @Override
          public String getDescription () {
            return "sick";
          }

          @Override
          public DateTime getStart () {
            return date ( "2016-01-01 00:00:00GMT" );
          }

          @Override
          public DateTime getEnd () {
            return date ( "2016-01-03 12:00:00GMT" );
          }
        } ) );
  }

  @SuppressWarnings ( "unchecked" )
  @Test
  @PrepareForTest ( { WhereIsKeywordProcessor.class, NotInQuestion.class, DateTime.class } )
  public void parseQuestion () throws Exception {
    WhereIsKeywordProcessor processor = new WhereIsKeywordProcessor ();
    processor.setLog ( new MockLogService () );
    final TimeZone GMT = TimeZone.getTimeZone ( "GMT" );

    assertThat ( processor.parseQuestion ( "where is @david", GMT ), allOf (
        isA ( (Class<Question>) WhereIsQuestion.class.asSubclass ( Question.class ) ),
        hasProperty ( "userName", equalTo ( "david" ) )
    ) );
    assertThat ( processor.parseQuestion ( "not in today", GMT ), allOf (
        isA ( (Class<Question>) NotInQuestion.class.asSubclass ( Question.class ) ),
        hasProperty ( "from", equalTo ( date ( "2016-01-01 00:00:00GMT" ) ) ),
        hasProperty ( "to", equalTo ( date ( "2016-01-02 00:00:00GMT" ) ) ),
        hasProperty ( "userName", nullValue () ),
        hasProperty ( "reason", equalTo ( "not in" ) )
    ) );
    assertThat ( processor.parseQuestion ( "not in tomorrow", GMT ), allOf (
        isA ( (Class<Question>) NotInQuestion.class.asSubclass ( Question.class ) ),
        hasProperty ( "from", equalTo ( date ( "2016-01-02 00:00:00GMT" ) ) ),
        hasProperty ( "to", equalTo ( date ( "2016-01-03 00:00:00GMT" ) ) ),
        hasProperty ( "userName", nullValue () ),
        hasProperty ( "reason", equalTo ( "not in" ) )
    ) );
    assertThat ( processor.parseQuestion ( "@david is not in", GMT ), allOf (
        isA ( (Class<Question>) NotInQuestion.class.asSubclass ( Question.class ) ),
        hasProperty ( "from", equalTo ( date ( "2016-01-01 00:00:00GMT" ) ) ),
        hasProperty ( "to", equalTo ( date ( "2016-01-02 00:00:00GMT" ) ) ),
        hasProperty ( "userName", equalTo ( "david" ) ),
        hasProperty ( "reason", equalTo ( "not in" ) )
    ) );
    assertThat ( processor.parseQuestion ( "@david is not in tomorrow", GMT ), allOf (
        isA ( (Class<Question>) NotInQuestion.class.asSubclass ( Question.class ) ),
        hasProperty ( "from", equalTo ( date ( "2016-01-02 00:00:00GMT" ) ) ),
        hasProperty ( "to", equalTo ( date ( "2016-01-03 00:00:00GMT" ) ) ),
        hasProperty ( "userName", equalTo ( "david" ) ),
        hasProperty ( "reason", equalTo ( "not in" ) )
    ) );
    assertThat ( processor.parseQuestion ( "@david is sick", GMT ), allOf (
        isA ( (Class<Question>) NotInQuestion.class.asSubclass ( Question.class ) ),
        hasProperty ( "from", equalTo ( date ( "2016-01-01 00:00:00GMT" ) ) ),
        hasProperty ( "to", equalTo ( date ( "2016-01-02 00:00:00GMT" ) ) ),
        hasProperty ( "userName", equalTo ( "david" ) ),
        hasProperty ( "reason", equalTo ( "sick" ) )
    ) );
    assertThat ( processor.parseQuestion ( "not in sunday", GMT ), allOf (
        isA ( (Class<Question>) NotInQuestion.class.asSubclass ( Question.class ) ),
        hasProperty ( "from", equalTo ( date ( "2016-01-03 00:00:00GMT" ) ) ),
        hasProperty ( "to", equalTo ( date ( "2016-01-04 00:00:00GMT" ) ) ),
        hasProperty ( "userName", nullValue () ),
        hasProperty ( "reason", equalTo ( "not in" ) )
    ) );
  }

  private DateTime date ( String date ) {
    return DateTime.parse ( date, DateTimeFormat.forPattern ( "yyyy-MM-dd HH:mm:ssz" ) );
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
    when ( lastTransport.getUserByUniqID ( anyString () ) ).then ( new Answer<User> () {
      @Override
      public User answer ( InvocationOnMock invocation ) throws Throwable {
        throw new java.lang.IllegalArgumentException ( (String) invocation.getArguments ()[0] );
      }
    } );
    return lastCommand;
  }

  private Command mockCommand ( String... words ) throws Transport.UserNotFoundException {
    return mockCommand ( true, words );
  }
}
