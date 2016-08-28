package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.transport.slack.Modal.BasePacket;
import au.id.vanlaatum.botter.transport.slack.Modal.RTM.Message;
import au.id.vanlaatum.botter.transport.slack.Modal.RTMStart;
import au.id.vanlaatum.botter.transport.slack.Modal.SlackTimeStamp;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

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

  private InputStream doPost ( String endpoint, Map<String, String> params ) throws IOException {
    log.log ( LogService.LOG_DEBUG, format ( "Sending post to {0} with {1}", endpoint, params ) );
    final URLConnection connection = slackURL.resolve ( endpoint ).toURL ().openConnection ( getProxy () );
    ( (HttpURLConnection) connection ).setRequestMethod ( "POST" );
    connection.setDoOutput ( true );
    try ( final DataOutputStream out = new DataOutputStream ( connection.getOutputStream () ) ) {
      out.writeBytes ( "token=" + token );
      for ( Map.Entry<String, String> entry : params.entrySet () ) {
        out.writeBytes ( "&" + entry.getKey () + "=" + entry.getValue () );
      }
      out.flush ();
    }

    return connection.getInputStream ();
  }

  protected RTMStart doRTMStart () throws IOException {
    RTMStart startData;
    try ( InputStream inputStream = doPost ( "rtm.start", Collections.<String, String>emptyMap () ) ) {
      final String json = IOUtils.toString ( inputStream );
      log.log ( LogService.LOG_DEBUG, format ( "Json is {0}", json ) );
      startData = mapper.readValue ( json, RTMStart.class );
    }
    return startData;
  }

  protected BasePacket doChannelMark ( String channel, SlackTimeStamp ts ) throws IOException {
    return doMark ( "channels.mark", channel, ts );
  }

  protected BasePacket doIMMark ( String channel, SlackTimeStamp ts ) throws IOException {
    return doMark ( "im.mark", channel, ts );
  }

  private BasePacket doMark ( String endpoint, String channel, SlackTimeStamp ts ) throws IOException {
    BasePacket rt;
    Map<String, String> params = new TreeMap<> ();
    params.put ( "channel", channel );
    params.put ( "ts", ts.toString () );
    try ( InputStream inputStream = doPost ( endpoint, params ) ) {
      final String json = IOUtils.toString ( inputStream );
      log.log ( LogService.LOG_DEBUG, format ( "Json is {0}", json ) );
      rt = mapper.readValue ( json, BasePacket.class );
      if ( !rt.getOk () ) {
        log.log ( LogService.LOG_WARNING, "Error marking " + channel + ": " + rt.getError () );
      }
    }
    return rt;
  }

  public BasePacket doChatUpdate ( Message message, List<Attachment> attachments ) throws IOException {
    BasePacket rt;
    Map<String, String> params = new TreeMap<> ();
    params.put ( "parse", "true" );
    params.put ( "ts", message.getTs ().toString () );
    params.put ( "text", message.getText () );
    params.put ( "channel", message.getChannel () );
    params.put ( "as_user", "false" );
    if ( attachments != null && !attachments.isEmpty () ) {
      params.put ( "attachments", mapper.writeValueAsString ( attachments ) );
    }
    try ( InputStream inputStream = doPost ( "chat.update", params ) ) {
      final String json = IOUtils.toString ( inputStream );
      log.log ( LogService.LOG_DEBUG, format ( "Json is {0}", json ) );
      rt = mapper.readValue ( json, BasePacket.class );
      if ( !rt.getOk () ) {
        log.log ( LogService.LOG_WARNING, "Error updating " + rt.getError () );
      }
    }
    return rt;
  }

  private Proxy getProxy () {
    if ( proxy != null ) {
      log.log ( LogService.LOG_DEBUG, "Using Proxy " + proxy );
      return new Proxy ( Proxy.Type.HTTP, new InetSocketAddress ( proxy.getHost (), proxy.getPort () ) );
    } else {
      log.log ( LogService.LOG_DEBUG, "No proxy" );
      return Proxy.NO_PROXY;
    }
  }

  public void setProxy ( URI proxy ) {
    this.proxy = proxy;
  }
}
