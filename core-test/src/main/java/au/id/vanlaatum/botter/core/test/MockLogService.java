package au.id.vanlaatum.botter.core.test;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class MockLogService implements LogService {
  @Override
  public void log ( int level, String message ) {
    log ( level, message, null );
  }

  @Override
  public void log ( int level, String message, Throwable exception ) {
    System.out.println ( message );
    if ( exception != null ) {
      exception.printStackTrace ( System.out );
    }
  }

  @Override
  public void log ( ServiceReference sr, int level, String message, Throwable exception ) {
    log ( level, message, exception );
  }

  @Override
  public void log ( ServiceReference sr, int level, String message ) {
    log ( level, message, null );
  }
}
