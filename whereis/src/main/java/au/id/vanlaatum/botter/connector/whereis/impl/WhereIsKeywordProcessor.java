package au.id.vanlaatum.botter.connector.whereis.impl;

import au.id.vanlaatum.botter.api.Command;
import au.id.vanlaatum.botter.api.KeyWordProcessor;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.connector.whereis.api.Location;
import au.id.vanlaatum.botter.connector.whereis.api.UserLocation;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.log.LogService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.BitSet;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static java.text.MessageFormat.format;

@Named ( "WhereIsKeyword" )
@Singleton
public class WhereIsKeywordProcessor implements KeyWordProcessor, ANTLRErrorListener {
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
    final Question question = parseQuestion ( message.getCommandText (), message.getUser ().getTimezone () );
    if ( question != null ) {
      switch ( question.getType () ) {
        case WHEREIS:
          processWhereIs ( (WhereIsQuestion) question, message );
          break;
        case NOTIN:
          processNotIn ( (NotInQuestion) question, message );
          break;
      }
    }
    return question != null;
  }

  private String mapUser ( Command message, String username ) throws Transport.UserNotFoundException {
    if ( StringUtils.isNoneBlank ( username ) ) {
      return message.getTransport ().getUser ( username ).getUniqID ();
    } else {
      return message.getUser ().getUniqID ();
    }
  }

  private void processNotIn ( NotInQuestion question, Command message ) {
    try {
      userLocation
          .addLocationForUser ( mapUser ( message, question.getUserName () ), question.getFrom (), question.getTo (),
              question.getReason () );
      message.reply ( "OK" );
    } catch ( Transport.UserNotFoundException e ) {
      message.error ( format ( "I don''t know who {0} is", question.getUserName () ) );
      log.log ( LogService.LOG_DEBUG, format ( "I don''t know who {0} is", question.getUserName () ), e );
    }
  }

  private void processWhereIs ( WhereIsQuestion question, Command message ) {
    try {
      final Location location =
          userLocation.getCurrentLocationForUser ( message.getTransport ().getUser ( question.getUserName () ).getUniqID (), new Date () );
      if ( location == null ) {
        message.reply ( String.format ( "I have no idea where %s is", question.getUserName () ) );
      } else {
        message.reply ( String.format ( "%s is %s", question.getUserName (), location.getDescription () ) );
      }
    } catch ( Transport.UserNotFoundException e ) {
      message.error ( format ( "I don''t know who {0} is", question.getUserName () ) );
      log.log ( LogService.LOG_DEBUG, format ( "I don''t know who {0} is", question.getUserName () ), e );
    }
  }

  @Override
  public void syntaxError ( Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg,
                            RecognitionException e ) {
    throw new RuntimeException ( line + ":" + charPositionInLine + ' ' + msg, e );
  }

  @Override
  public void reportAmbiguity ( Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts,
                                ATNConfigSet configs ) {

  }

  @Override
  public void reportAttemptingFullContext ( Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts,
                                            ATNConfigSet configs ) {

  }

  @Override
  public void reportContextSensitivity ( Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs ) {

  }

  Question parseQuestion ( String text, TimeZone timezone ) {
    QuestionLexer lexer = new QuestionLexer ( new CaseInsensitiveInputStream ( text ) );
    QuestionParser parser = new QuestionParser ( new BufferedTokenStream ( lexer ) );
    lexer.removeErrorListeners ();
    lexer.addErrorListener ( this );
    parser.removeErrorListeners ();
    parser.addErrorListener ( this );
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
