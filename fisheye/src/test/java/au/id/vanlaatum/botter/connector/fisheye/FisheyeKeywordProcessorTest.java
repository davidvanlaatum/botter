package au.id.vanlaatum.botter.connector.fisheye;

import au.id.vanlaatum.botter.api.Command;
import au.id.vanlaatum.botter.connector.fisheye.api.ChangeSet;
import au.id.vanlaatum.botter.connector.fisheye.api.FisheyeConnector;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class FisheyeKeywordProcessorTest implements LogService {
  @Test
  public void checkForKeywords () throws Exception {
    final FisheyeConnector connector = mock ( FisheyeConnector.class );
    when ( connector.urlMatches ( new URI ( "http://fisheye.rpdata.local/changelog/svn.adl.vm?cs=119696" ) ) )
        .thenReturn ( true );
    when ( connector.removePrefix ( new URI ( "http://fisheye.rpdata.local/changelog/svn.adl.vm?cs=119696" ) ) )
        .thenReturn ( new URI ( "changelog/svn.adl.vm?cs=119696" ) );
    ChangeSet changeSet = mock ( ChangeSet.class );
    when ( changeSet.getID () ).thenReturn ( "119696" );
    when ( changeSet.getRepositoryName () ).thenReturn ( "svn.adl.vm" );
    when ( changeSet.getAuthor () ).thenReturn ( "jenkins" );
    when ( changeSet.getComment () ).thenReturn ( "bootstrap" );
    when ( changeSet.getDate () ).thenReturn ( new Date ( 0 ) );
    when ( changeSet.getFiles () ).thenReturn ( Arrays.<ChangeSet.File>asList ( new ChangeSet.File () {
      @Override
      public String getPath () {
        return "vx/trunk/include/classes/core/bootstrap_cache.php";
      }
    }, new ChangeSet.File () {

      @Override
      public String getPath () {
        return "bla.php";
      }
    } ) );
    when ( connector.getChangeSet ( "svn.adl.vm", "119696" ) ).thenReturn ( changeSet );
    FisheyeKeywordProcessor processor = new FisheyeKeywordProcessor ();
    processor.setConnectors ( Collections.singletonList ( connector ) );
    final Command command = mock ( Command.class );
    when ( command.getCommandParts () )
        .thenReturn ( Collections.singletonList ( "http://fisheye.rpdata.local/changelog/svn.adl.vm?cs=119696" ) );
    processor.setLog ( this );
    assertTrue ( processor.checkForKeywords ( command, Collections.<Boolean>emptyList () ) );
    verify ( connector ).getChangeSet ( "svn.adl.vm", "119696" );
    verify ( command )
        .reply ( Mockito.matches ( "svn.adl.vm@119696 by jenkins at " + new Date ( 0 ).toString () +
            "\nbootstrap\n" +
            "  vx/trunk/include/classes/core/bootstrap_cache.php\n" +
            "  bla.php" ) );
  }

  @Override
  public void log ( int level, String message ) {
    log ( level, message, null );
  }

  @Override
  public void log ( int level, String message, Throwable exception ) {
    System.out.println ( message );
    if ( exception != null ) {
      exception.printStackTrace ();
    }
  }

  @Override
  public void log ( ServiceReference sr, int level, String message ) {
    log ( level, message );
  }

  @Override
  public void log ( ServiceReference sr, int level, String message, Throwable exception ) {
    log ( level, message, exception );
  }
}
