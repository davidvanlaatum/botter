package au.id.vanlaatum.botter.connector.whereis.model;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ResourceAccessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

import static java.util.Objects.requireNonNull;

@Singleton
@Named ( "LiquibaseRunner" )
public class LiquibaseRunner implements ServiceListener {
  public static final String FILTER = "(osgi.jndi.service.name=botter-whereis)";
  @Inject
  @Named ( "blueprintBundleContext" )
  private BundleContext context;
  @Inject
  @Named ( "logService" )
  private LogService log;

  @PostConstruct
  public void start () throws InvalidSyntaxException {
    context.addServiceListener ( this, FILTER );
  }

  @PreDestroy
  public void stop () {
    context.removeServiceListener ( this );
  }

  @Override
  public void serviceChanged ( ServiceEvent event ) {
    if ( event.getType () == ServiceEvent.MODIFIED || event.getType () == ServiceEvent.REGISTERED ) {
      @SuppressWarnings ( "unchecked" )
      final ServiceReference<DataSource> serviceReference = (ServiceReference<DataSource>) event.getServiceReference ();
      final DataSource dataSource = context.getService ( serviceReference );
      runMigration ( dataSource );
      context.ungetService ( event.getServiceReference () );
    }
  }

  synchronized private void runMigration ( DataSource dataSource ) {
    try ( Connection connection = dataSource.getConnection () ) {
      log.log ( LogService.LOG_INFO, "Running liquibase" );
      final Liquibase liquibase = new Liquibase ( "OSGI-INF/liquibase/master.xml", new ResourceAccessor () {
        @Override
        public Set<InputStream> getResourcesAsStream ( String s ) throws IOException {
          requireNonNull ( context, "Context Null" );
          return Collections.singleton ( requireNonNull ( context.getBundle ().getEntry ( s ), "Failed to open " + s ).openStream () );
        }

        @Override
        public Set<String> list ( String s, String s1, boolean b, boolean b1, boolean b2 ) throws IOException {
          throw new UnsupportedOperationException ( "Not implemented" );
        }

        @Override
        public ClassLoader toClassLoader () {
          throw new UnsupportedOperationException ( "Not implemented" );
        }
      }, new JdbcConnection ( connection ) );
      liquibase.update ( (String) null );
    } catch ( SQLException | LiquibaseException e ) {
      log.log ( LogService.LOG_ERROR, "Liquibase Exception", e );
    }
  }
}
