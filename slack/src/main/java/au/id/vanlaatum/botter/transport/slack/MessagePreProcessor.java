package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.transport.slack.modal.SlackUser;
import org.osgi.service.log.LogService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagePreProcessor {
  public static final Pattern PATTERN = Pattern.compile ( "<(.*?)>" );
  public static final Pattern URL = Pattern.compile ( "(\\w+)://(\\S*)(:\\d+)*(/\\S+)" );
  private final SlackTransport transport;
  private LogService log;

  public MessagePreProcessor ( SlackTransport transport ) {
    this.transport = transport;
  }

  public MessagePreProcessor setLog ( LogService log ) {
    this.log = log;
    return this;
  }

  protected String convertText ( String text, SlackMessageDTO dto ) {
    StringBuffer out = new StringBuffer ();
    if ( text != null ) {
      final Matcher matcher = PATTERN.matcher ( text );
      while ( matcher.find () ) {
        final String exp = matcher.group ( 1 );
        if ( exp.startsWith ( "@" ) ) {
          try {
            final SlackUser user = transport.getUsers ().getUser ( exp.substring ( 1 ) );
            matcher.appendReplacement ( out, "@" + user.getName () );
          } catch ( Transport.UserNotFoundException ignore ) {
            log.log ( LogService.LOG_DEBUG, "User " + exp.substring ( 1 ) + " not found", ignore );
          }
        } else if ( URL.matcher ( exp ).matches () ) {
          matcher.appendReplacement ( out, exp.replaceFirst ( "\\|", " " ) );
        }
      }
      matcher.appendTail ( out );
    }
    return out.toString ();
  }
}
