package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.transport.slack.Modal.RTMStart;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.osgi.service.log.LogService;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URLConnection;
import java.util.Objects;

import static java.text.MessageFormat.format;

public class API {

  private URI slackURL;
  private LogService log;
  private String token;
  private ObjectMapper mapper = new ObjectMapper ();
  private URI proxy;

  public API ( URI slackURL, LogService log ) {
    this.slackURL = slackURL;
    this.log = log;
    mapper.disable ( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES );
  }

  public boolean setToken ( String token ) {
    boolean rt = !Objects.equals ( this.token, token );
    this.token = token;
    return rt;
  }

  protected RTMStart doRTMStart () throws IOException {
    final URLConnection connection = slackURL.resolve ( "rtm.start" ).toURL ().openConnection ( getProxy () );
    ( (HttpURLConnection) connection ).setRequestMethod ( "POST" );
    connection.setDoOutput ( true );
    try ( final DataOutputStream out = new DataOutputStream ( connection.getOutputStream () ) ) {
      out.writeBytes ( "token=" + token );
      out.flush ();
    }
    RTMStart startData;
    try ( InputStream inputStream = connection.getInputStream () ) {
      final String json = IOUtils.toString ( inputStream );
      log.log ( LogService.LOG_INFO, format ( "Json is {0}", json ) );
      startData = mapper.readValue ( json, RTMStart.class );
    }
    return startData;
  }

  private Proxy getProxy () {
    if ( proxy != null ) {
      log.log ( LogService.LOG_INFO, "Using Proxy " + proxy );
      return new Proxy ( Proxy.Type.HTTP, new InetSocketAddress ( proxy.getHost (), proxy.getPort () ) );
    } else {
      log.log ( LogService.LOG_INFO, "No proxy" );
      return Proxy.NO_PROXY;
    }
  }

  public void setProxy ( URI proxy ) {
    this.proxy = proxy;
  }
}
