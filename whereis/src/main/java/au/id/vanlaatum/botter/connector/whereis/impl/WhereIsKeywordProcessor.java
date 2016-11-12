package au.id.vanlaatum.botter.connector.whereis.impl;

import au.id.vanlaatum.botter.antlr.utils.CaseInsensitiveInputStream;
import au.id.vanlaatum.botter.antlr.utils.ErrorToExceptionErrorListener;
import au.id.vanlaatum.botter.api.Command;
import au.id.vanlaatum.botter.api.KeyWordProcessor;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.api.User;
import au.id.vanlaatum.botter.connector.whereis.api.Location;
import au.id.vanlaatum.botter.connector.whereis.api.UserLocation;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.osgi.service.log.LogService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static java.text.MessageFormat.format;
import static java.util.Objects.requireNonNull;

@Named ( "WhereIsKeyword" )
@Singleton
public class WhereIsKeywordProcessor implements KeyWordProcessor {
  @Inject
  @Named ( "logService" )
  private LogService log;
  @Inject
  private UserLocation userLocation;

  public WhereIsKeywordProcessor setUserLocation ( UserLocation userLocation ) {
    this.userLocation = userLocation;
    return this;
  }

  public WhereIsKeywordProcessor setLog ( LogService log ) {
    this.log = log;
    return this;
  }

  @Override
  public boolean checkForKeywords ( Command message, List<Boolean> used ) {
    Question question = null;
    try {
      question = parseQuestion ( message.getCommandText (), message.getUser ().getTimezone () );
    } catch ( RuntimeException ex ) {
      log.log ( LogService.LOG_WARNING, null, ex );
    }
    if ( question != null ) {
      try {
        switch ( question.getType () ) {
          case WHEREIS:
            processWhereIs ( (WhereIsQuestion) question, message );
            break;
          case NOTIN:
            processNotIn ( (NotInQuestion) question, message );
            break;
        }
      } catch ( Exception e ) {
        log.log ( LogService.LOG_WARNING, "Unhandled exception in " + getClass ().getSimpleName (), e );
        message.error ( e );
      }
    }
    return question != null;
  }

  private User mapUser ( Command message, String username ) throws Transport.UserNotFoundException {
    if ( StringUtils.isNoneBlank ( username ) ) {
      return message.getTransport ().getUser ( username );
    } else {
      return message.getUser ();
    }
  }

  private void processNotIn ( NotInQuestion question, Command message ) {
    try {
      final User user = mapUser ( message, question.getUserName () );
      Location location =
          userLocation.addLocationForUser ( user.getUniqID (), question.getFrom (), question.getTo (),
              question.getReason () );
      message.reply ( format ( "ok I have set {3} location to {0} between {1} and {2}", location.getDescription (),
          formatTime ( location.getStart (), message, user.getUniqID () ),
          formatTime ( location.getEnd (), message, user.getUniqID () ),
          message.getUser () == user ? "your" : user.getName () + "'s" ) );
    } catch ( Transport.UserNotFoundException e ) {
      message.error ( format ( "I don''t know who {0} is", question.getUserName () ) );
      log.log ( LogService.LOG_DEBUG, format ( "I don''t know who {0} is", question.getUserName () ), e );
    }
  }

  private TimeZone getUserTimeZone ( Transport transport, String userId ) throws Transport.UserNotFoundException {
    final User user = requireNonNull ( transport.getUserByUniqID ( userId ), "user null" );
    return user.getTimezone ();
  }

  private String formatTime ( DateTime time, Command message, String userId ) {
    String rt;
    try {
      final MutableDateTime dateTime = time.toMutableDateTime ();
      dateTime.setZone ( DateTimeZone.forTimeZone ( getUserTimeZone ( message.getTransport (), userId ) ) );
      if ( time.isAfter ( dateTime.copy ().dayOfMonth ().roundFloor () ) ) {
        rt = dateTime.toString ( new DateTimeFormatterBuilder ().appendPattern ( "hh:mma dd-MM-yyyyZ" ).toFormatter () );
      } else {
        rt = dateTime.toString ( new DateTimeFormatterBuilder ().appendPattern ( "dd-MM-yyyy" ).toFormatter () );
      }
    } catch ( Transport.UserNotFoundException e ) {
      log.log ( LogService.LOG_WARNING, null, e );
      rt = time.toString ();
    }
    return rt;
  }

  void processWhereIs ( WhereIsQuestion question, Command message ) {
    try {
      final Location location =
          userLocation.getCurrentLocationForUser ( message.getTransport ().getUser ( question.getUserName () ).getUniqID (), new Date () );
      if ( location == null ) {
        message.reply ( String.format ( "I have no idea where %s is", question.getUserName () ) );
      } else {
        message.reply ( getWhereIsReply ( question, message, location ) );
      }
    } catch ( Transport.UserNotFoundException e ) {
      message.error ( format ( "I don''t know who {0} is", question.getUserName () ) );
      log.log ( LogService.LOG_DEBUG, format ( "I don''t know who {0} is", question.getUserName () ), e );
    }
  }

  String getWhereIsReply ( WhereIsQuestion question, Command message, Location location ) throws Transport.UserNotFoundException {
    final DateTime tomorrow =
        DateTime.now ( DateTimeZone.forTimeZone ( getUserTimeZone ( message.getTransport (), location.getUser ().getUniqID () ) ) )
            .plusDays ( 1 ).dayOfMonth ()
            .roundFloorCopy ();
    String until = location.getEnd ().isAfter ( tomorrow ) ? " until " +
        formatTime ( location.getEnd (), message, location.getUser ().getUniqID () ) : "";
    return String.format ( "%s is %s%s", question.getUserName (), location.getDescription (), until );
  }

  Question parseQuestion ( String text, TimeZone timezone ) {
    QuestionLexer lexer = new QuestionLexer ( new CaseInsensitiveInputStream ( text ) );
    QuestionParser parser = new QuestionParser ( new BufferedTokenStream ( lexer ) );
    lexer.removeErrorListeners ();
    lexer.addErrorListener ( new ErrorToExceptionErrorListener () );
    parser.setErrorHandler ( new BailErrorStrategy () );
    QuestionParser.QuestionContext question = parser.question ();
    Question rt = null;
    if ( question != null && question.exception == null ) {
      switch ( question.type ) {
        case WHEREIS:
          rt = new WhereIsQuestion ( question.whereisquestion ().userName );
          break;
        case NOTIN:
          rt = new NotInQuestion ( question.notinquestion (), timezone );
          break;
      }
    }
    return rt;
  }
}
