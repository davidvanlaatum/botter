package au.id.vanlaatum.botter.connector.fisheye.impl;

import au.id.vanlaatum.botter.api.Command;
import au.id.vanlaatum.botter.api.KeyWordProcessor;
import au.id.vanlaatum.botter.connector.fisheye.api.ChangeSet;
import au.id.vanlaatum.botter.connector.fisheye.api.FisheyeConnector;
import au.id.vanlaatum.botter.connector.fisheye.api.RemoteCallFailedException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.osgi.service.log.LogService;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static java.text.MessageFormat.format;


@OsgiServiceProvider ( classes = KeyWordProcessor.class )
@Named ( "FisheyeKeyword" )
public class FisheyeKeywordProcessor implements KeyWordProcessor {

  @Inject
  @Named ( "Connectors" )
  private List<FisheyeConnector> connectors;
  @Inject
  @Named ( "logService" )
  private LogService log;

  public FisheyeKeywordProcessor setConnectors ( List<FisheyeConnector> connectors ) {
    this.connectors = connectors;
    return this;
  }

  public FisheyeKeywordProcessor setLog ( LogService log ) {
    this.log = log;
    return this;
  }

  @Override
  public boolean checkForKeywords ( Command message, List<Boolean> used ) {
    boolean rt = false;
    for ( int i = 0; i < message.getCommandParts ().size (); i++ ) {
      if ( message.getCommandParts ().get ( i ).startsWith ( "http" ) ) {
        try {
          URI uri = new URI ( message.getCommandParts ().get ( i ) );
          for ( FisheyeConnector connector : connectors ) {
            if ( connector.urlMatches ( uri ) ) {
              rt = decodeAndProcess ( connector, uri, message );
              break;
            }
          }
        } catch ( URISyntaxException ignore ) {
          log.log ( LogService.LOG_DEBUG, "Failed to parse URI " + message.getCommandParts ().get ( i ), ignore );
        }
      }
    }
    return rt;
  }

  private boolean decodeAndProcess ( FisheyeConnector connector, URI uri, Command message ) {
    boolean rt = false;
    URI path = connector.removePrefix ( uri );
    if ( path.getPath ().startsWith ( "changelog/" ) ) {
      final String repo = path.getPath ().substring ( "changelog/".length () );
      final List<NameValuePair> params = URLEncodedUtils.parse ( uri, "UTF-8" );
      String changeSet = null;
      for ( NameValuePair pair : params ) {
        if ( Objects.equals ( pair.getName (), "cs" ) ) {
          changeSet = pair.getValue ();
        }
      }
      log.log ( LogService.LOG_INFO, format ( "Repo is {0} change set is {1}", repo, changeSet ) );
      if ( changeSet != null ) {
        rt = processChangeSet ( connector, message, repo, changeSet );
      }
    }
    return rt;
  }

  private boolean processChangeSet ( FisheyeConnector connector, Command message, String repo, String changeSet ) {
    boolean rt = false;
    try {
      final ChangeSet set = connector.getChangeSet ( repo, changeSet );
      StringBuilder buffer = new StringBuilder ();
      buffer.append ( set.getRepositoryName () ).append ( "@" ).append ( set.getID () ).append ( " by " )
          .append ( set.getAuthor () ).append ( " at " ).append ( set.getDate () ).append ( "\n" )
          .append ( set.getComment () ).append ( "\n" );

      for ( ChangeSet.File file : set.getFiles () ) {
        buffer.append ( "  " ).append ( file.getPath () ).append ( "\n" );
      }
      message.annotate ( buffer.toString ().trim () );
      rt = true;
    } catch ( IOException | RemoteCallFailedException e ) {
      log.log ( LogService.LOG_WARNING, "Exception fetching changeset", e );
    }
    return rt;
  }
}
