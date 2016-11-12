package au.id.vanlaatum.botter.connector.fisheye.impl;

import au.id.vanlaatum.botter.connector.fisheye.api.ChangeSet;
import au.id.vanlaatum.botter.connector.fisheye.api.FisheyeConnector;
import au.id.vanlaatum.botter.connector.fisheye.api.RemoteCallFailedException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.log.LogService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Dictionary;
import java.util.Hashtable;

import static java.text.MessageFormat.format;

public class FisheyeConnectorImpl implements FisheyeConnector {
  public static final String URL = "url";
  public static final String ENABLED = "enabled";
  private final String pid;
  private boolean enabled;
  private BundleContext context;
  private LogService log;
  private ServiceRegistration<FisheyeConnector> connectorRegistration;
  private URI uri;
  private HttpClientBuilder clientBuilder;
  private CloseableHttpClient client;
  private ObjectMapper mapper = new ObjectMapper ();


  public FisheyeConnectorImpl ( String pid ) {
    this.pid = pid;
    mapper.disable ( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES );
  }

  @Override
  public boolean isEnabled () {
    return enabled;
  }

  public FisheyeConnectorImpl setURI ( URI uri ) {
    this.uri = uri;
    return this;
  }

  public FisheyeConnectorImpl setClient ( CloseableHttpClient client ) {
    this.client = client;
    return this;
  }

  @Override
  public ChangeSet getChangeSet ( String repo, String id ) throws IOException, RemoteCallFailedException {
    log.log ( LogService.LOG_INFO, format ( "Fetching info for changeset {0} from repo {1}", id, repo ) );
    final HttpGet get = new HttpGet ( uri.resolve ( "/rest-service-fe/revisionData-v1/changeset/" ).resolve ( repo + "/" ).resolve ( id ) );
    get.addHeader ( "Accept", "application/json" );
    try ( final CloseableHttpResponse response = client.execute ( get ) ) {
      ChangeSetImpl changeSet = null;
      if ( response.getStatusLine ().getStatusCode () == HttpURLConnection.HTTP_OK ) {
        try ( final InputStream content = response.getEntity ().getContent () ) {
          changeSet = mapper.readValue ( content, ChangeSetImpl.class );
        }
      } else {
        throw new RemoteCallFailedException ();
      }
      return changeSet;
    } finally {
      get.releaseConnection ();
    }
  }

  @Override
  public boolean urlMatches ( URI uri ) {
    final URI rel = this.uri.relativize ( uri );
    return !rel.isAbsolute ();
  }

  @Override
  public URI removePrefix ( URI uri ) {
    return this.uri.relativize ( uri );
  }

  @Override
  public String getName () {
    return uri.toString ();
  }

  public void updated ( Dictionary<String, ?> dictionary ) throws ConfigurationException {
    log.log ( LogService.LOG_INFO, format ( "Fisheye {0} receiving config {1}", pid, dictionary ) );
    try {
      uri = new URI ( (String) dictionary.get ( URL ) );
    } catch ( URISyntaxException e ) {
      throw new ConfigurationException ( URL, "Invalid URL", e );
    }
    enabled = (Boolean) dictionary.get ( ENABLED );

    if ( client != null ) {
      try {
        client.close ();
      } catch ( IOException e ) {
        log.log ( LogService.LOG_WARNING, "Failed to close client", e );
      }
    }
    client = clientBuilder.setUserAgent ( "Botter Fisheye Connector" ).build ();
    registerService ();
  }

  protected void registerService () {
    if ( connectorRegistration == null ) {
      connectorRegistration = context.registerService ( FisheyeConnector.class, this, buildServiceProperties () );
    } else {
      connectorRegistration.setProperties ( buildServiceProperties () );
    }
  }

  private Dictionary<String, ?> buildServiceProperties () {
    Dictionary<String, Object> prop = new Hashtable<> ();
    prop.put ( Constants.SERVICE_ID, pid );
    prop.put ( Constants.SERVICE_DESCRIPTION, format ( "Connector for fisheye {0}", uri ) );
    return prop;
  }

  public void shutdown () {
    if ( connectorRegistration != null ) {
      connectorRegistration.unregister ();
    }
  }

  public FisheyeConnectorImpl setContext ( BundleContext context ) {
    this.context = context;
    return this;
  }

  public FisheyeConnectorImpl setLog ( LogService log ) {
    this.log = log;
    return this;
  }

  public FisheyeConnectorImpl setClientBuilder ( HttpClientBuilder clientBuilder ) {
    this.clientBuilder = clientBuilder;
    return this;
  }
}
