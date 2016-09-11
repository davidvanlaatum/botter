package au.id.vanlaatum.botter.connector.fisheye.impl;

import au.id.vanlaatum.botter.api.Attribute;
import au.id.vanlaatum.botter.api.BotFactory;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.ops4j.pax.cdi.api.OsgiService;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Singleton
@Named ( "FisheyeFactory" )
public class FisheyeFactory implements ManagedServiceFactory, MetaTypeProvider {
  private final Map<String, FisheyeConnectorImpl> connectors = new TreeMap<> ();
  @Inject
  @Named ( "blueprintBundleContext" )
  private BundleContext context;
  @Inject
  @Named ( "logService" )
  private LogService log;
  @Inject
  @OsgiService
  private BotFactory botFactory;
  @Inject
  @Named ( "httpClient" )
  private HttpClientBuilderFactory httpClientBuilderFactory;

  @Override
  public String getName () {
    return "Fisheye Connector";
  }

  @Override
  public void updated ( String pid, Dictionary<String, ?> dictionary ) throws ConfigurationException {
    FisheyeConnectorImpl connector = connectors.get ( pid );
    if ( connector == null ) {
      connector = new FisheyeConnectorImpl ( pid ).setContext ( context ).setLog ( log )
          .setClientBuilder ( httpClientBuilderFactory.newBuilder () );
      connectors.put ( pid, connector );
    }
    connector.updated ( dictionary );
  }

  @Override
  public void deleted ( String pid ) {
    FisheyeConnectorImpl connector = connectors.remove ( pid );
    if ( connector != null ) {
      connector.shutdown ();
    }
  }

  @Override
  public ObjectClassDefinition getObjectClassDefinition ( final String id, String locale ) {
    return new ObjectClassDefinition () {
      @Override
      public String getName () {
        return "Botter Fisheye Connector";
      }

      @Override
      public String getID () {
        return id;
      }

      @Override
      public String getDescription () {
        return "Botter Fisheye Connector";
      }

      @Override
      public AttributeDefinition[] getAttributeDefinitions ( int filter ) {
        List<AttributeDefinition> options = new ArrayList<> ();
        if ( filter == ALL || filter == REQUIRED ) {
          options.add ( new Attribute ().setId ( FisheyeConnectorImpl.URL ).setName ( "URL" )
              .setDescription ( "base URL of fisheye" ) );
          options.add ( new Attribute ().setId ( FisheyeConnectorImpl.ENABLED ).setName ( "Enabled" )
              .setType ( AttributeDefinition.BOOLEAN ).setDefaultValue ( "true" ) );
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
