package au.id.vanlaatum.botter.connector.whereis.model;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ResourceAccessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

@Singleton
@Named ( "LiquibaseRunner" )
public class LiquibaseRunner implements ServiceTrackerCustomizer<DataSource, DataSource> {
  private static final String FILTER = "(&(osgi.jndi.service.name=botter-whereis)(!(liquibase=*)))";
  @Inject
  @Named ( "blueprintBundleContext" )
  private BundleContext context;
  @Inject
  @Named ( "logService" )
  private LogService log;
  private ServiceTracker<DataSource, DataSource> tracker;
  private ServiceReference<DataSource> dataSourceServiceReference;
  private ServiceRegistration<DataSource> dataSourceServiceRegistration;
  private DataSource dataSource;

  @PostConstruct
  public void start () throws InvalidSyntaxException {
    tracker = new ServiceTracker<> ( context, context.createFilter ( FILTER ), this );
    tracker.open ();
  }

  @PreDestroy
  public void stop () {
    tracker.close ();
    tracker = null;
  }

  private Dictionary<String, Object> buildServiceProperties ( ServiceReference<DataSource> serviceReference ) {
    Dictionary<String, Object> prop = new Hashtable<> ();
    for ( String key : serviceReference.getPropertyKeys () ) {
      String newKey = key;
      if ( key.startsWith ( "service." ) ) {
        newKey = "origin." + key;
      }
      if ( !Objects.equals ( key, "objectClass" ) ) {
        prop.put ( newKey, serviceReference.getProperty ( key ) );
      }
    }
    prop.put ( "liquibase", true );
    return prop;
  }

  private synchronized boolean runMigration ( DataSource dataSource ) {
    try ( Connection connection = dataSource.getConnection () ) {
      log.log ( LogService.LOG_INFO, "Running liquibase" );
      final Liquibase liquibase = new Liquibase ( "OSGI-INF/liquibase/master.xml", new ResourceAccessor () {
        @Override
        public Set<InputStream> getResourcesAsStream ( String s ) throws IOException {
          requireNonNull ( context, "Context Null" );
          final URL url = context.getBundle ().getEntry ( s );
          return url != null ? Collections.singleton ( url.openStream () ) : null;
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
      log.log ( LogService.LOG_INFO, "Liquibase complete" );
      return true;
    } catch ( Throwable ex ) {
      log.log ( LogService.LOG_ERROR, "Liquibase failed", ex );
      return false;
    }
  }

  @Override
  public DataSource addingService ( ServiceReference<DataSource> reference ) {
    if ( dataSourceServiceReference == null ) {
      log.log ( LogService.LOG_DEBUG, "Registering datasource" );
      dataSource = context.getService ( reference );
      if ( runMigration ( dataSource ) ) {
        dataSourceServiceReference = reference;
        final Dictionary<String, Object> properties = buildServiceProperties ( reference );
        log.log ( LogService.LOG_DEBUG, "Datasource properties are " + properties );
        dataSourceServiceRegistration = context.registerService ( DataSource.class, dataSource, properties );
      } else {
        context.ungetService ( reference );
        dataSource = null;
      }
    }
    return dataSourceServiceReference == reference ? context.getService ( reference ) : null;
  }

  @Override
  public void modifiedService ( ServiceReference<DataSource> reference, DataSource service ) {
    if ( dataSourceServiceReference == reference ) {
      final Dictionary<String, Object> properties = buildServiceProperties ( reference );
      log.log ( LogService.LOG_DEBUG, "Updating datasource properties to " + properties );
      dataSourceServiceRegistration.setProperties ( properties );
    }
  }

  @Override
  public void removedService ( ServiceReference<DataSource> reference, DataSource service ) {
    if ( dataSourceServiceReference == reference ) {
      log.log ( LogService.LOG_DEBUG, "Un-registering datasource" );
      dataSourceServiceRegistration.unregister ();
      dataSource = null;
      dataSourceServiceReference = null;
      dataSourceServiceRegistration = null;
    }
  }
}
