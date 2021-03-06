package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.api.Attribute;
import au.id.vanlaatum.botter.api.BotFactory;
import org.glassfish.tyrus.client.ClientManager;
import org.ops4j.pax.cdi.api.OsgiService;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Named ( "SlackTransportFactory" )
public class SlackTransportFactory implements ManagedServiceFactory, MetaTypeProvider {
  private final Map<String, SlackTransport> transports = new ConcurrentHashMap<> ();
  private final URI slackURL = new URI ( "https://slack.com/api/" );
  @Inject
  @Named ( "blueprintBundleContext" )
  private BundleContext context;
  @Inject
  @Named ( "logService" )
  private LogService log;
  @Inject
  @OsgiService
  private BotFactory botFactory;

  public SlackTransportFactory () throws URISyntaxException {
  }

  @PostConstruct
  public void startup () {

  }

  @PreDestroy
  public void shutdown () {
    for ( SlackTransport slackTransport : transports.values () ) {
      slackTransport.disconnect ();
      slackTransport.shutdown ();
    }
  }

  @Override
  public String getName () {
    return "Slack Transport";
  }

  @Override
  public void updated ( String pid, Dictionary<String, ?> dictionary ) throws ConfigurationException {
    synchronized ( transports ) {
      if ( transports.containsKey ( pid ) ) {
        transports.get ( pid ).updated ( dictionary );
      } else {
        SlackTransport transport = new SlackTransport ()
            .setLog ( log )
            .setContext ( context )
            .setBotFactory ( botFactory )
            .setApi ( new API ( slackURL, log ) )
            .setClientManager ( ClientManager.createClient () );
        transport.updated ( dictionary );
        transports.put ( pid, transport );
      }
    }
  }

  @Override
  public void deleted ( String s ) {
    synchronized ( transports ) {
      if ( transports.containsKey ( s ) ) {
        final SlackTransport transport = transports.get ( s );
        transport.disconnect ();
      }
    }
  }

  @Override
  public ObjectClassDefinition getObjectClassDefinition ( final String id, final String locale ) {
    return new ObjectClassDefinition () {
      @Override
      public String getName () {
        return "Botter Slack Transport";
      }

      @Override
      public String getID () {
        return id;
      }

      @Override
      public String getDescription () {
        return "Botter Slack Transport";
      }

      @Override
      public AttributeDefinition[] getAttributeDefinitions ( int filter ) {
        List<AttributeDefinition> options = new ArrayList<> ();
        if ( filter == ALL || filter == REQUIRED ) {
          options.add ( new Attribute ().setId ( SlackTransport.TOKEN ).setName ( "Token" ).setDescription ( "API Token" ) );
          options.add ( new Attribute ().setId ( SlackTransport.ENABLED ).setName ( "Enabled" )
              .setType ( AttributeDefinition.BOOLEAN ).setDefaultValue ( "true" ) );
        }
        if ( filter == ALL || filter == OPTIONAL ) {
          options.add ( new Attribute ().setId ( SlackTransport.PING_INTERVAL ).setName ( "Ping Interval" )
              .setDescription ( "Time between ping packets to slack servers" ).setType ( AttributeDefinition.LONG )
              .setDefaultValue ( String.valueOf ( SlackTransport.PING_INTERVAL_DEFAULT ) ) );
          options.add ( new Attribute ().setId ( SlackTransport.PROXY_URI )
              .setDescription ( "Proxy url http://host:port" ).setName ( "Proxy" ) );
        }
        return options.toArray ( new AttributeDefinition[options.size ()] );
      }

      @Override
      public InputStream getIcon ( int size ) throws IOException {
        return null;
      }
    };
  }

  @Override
  public String[] getLocales () {
    return new String[]{ "en" };
  }

}
