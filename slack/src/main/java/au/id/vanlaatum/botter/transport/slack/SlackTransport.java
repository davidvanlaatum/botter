package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.api.BotFactory;
import au.id.vanlaatum.botter.api.StatusInfoProvider;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.transport.slack.Modal.RTM.BaseEvent;
import au.id.vanlaatum.botter.transport.slack.Modal.RTM.BasePacket;
import au.id.vanlaatum.botter.transport.slack.Modal.RTM.Marked;
import au.id.vanlaatum.botter.transport.slack.Modal.RTM.Message;
import au.id.vanlaatum.botter.transport.slack.Modal.RTM.Ping;
import au.id.vanlaatum.botter.transport.slack.Modal.RTM.Pong;
import au.id.vanlaatum.botter.transport.slack.Modal.RTM.ReconnectURL;
import au.id.vanlaatum.botter.transport.slack.Modal.RTM.TeamJoin;
import au.id.vanlaatum.botter.transport.slack.Modal.RTM.UserChange;
import au.id.vanlaatum.botter.transport.slack.Modal.RTMStart;
import au.id.vanlaatum.botter.transport.slack.Modal.Self;
import au.id.vanlaatum.botter.transport.slack.Modal.SlackChannel;
import au.id.vanlaatum.botter.transport.slack.Modal.SlackIM;
import au.id.vanlaatum.botter.transport.slack.Modal.SlackTeam;
import au.id.vanlaatum.botter.transport.slack.Modal.SlackUser;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static java.text.MessageFormat.format;

public class SlackTransport implements Transport, ManagedService {
  static final String PING_INTERVAL = "ping.interval";
  static final long PING_INTERVAL_DEFAULT = 10000;
  static final String PROXY_URI = "proxy.url";
  private final BundleContext context;
  private final LogService log;
  private final BotFactory botFactory;
  private final Users users = new Users ();
  private final Channels channels = new Channels ();
  private final API api;
  private final Map<String, Integer> inPackets = new TreeMap<> ();
  private final Map<String, Integer> outPackets = new TreeMap<> ();
  private String pid;
  private ClientManager clientManager;
  private Session session;
  private boolean open;
  private ServiceRegistration<Transport> registration;
  private ServiceRegistration<StatusInfoProvider> providerRegistration;
  private SlackTeam team;
  private Self self;
  private MessagePreProcessor messagePreProcessor = new MessagePreProcessor ( this );
  private int msgId = 0;
  private Map<Integer, BaseEvent> pendingMessages = new ConcurrentHashMap<> ();
  private DelayQueue<RetryTimer> retryTimers = new DelayQueue<> ();
  private Date lastPacket = new Date ();
  private Date lastPing = new Date ();
  private long rtt;
  private SlackMessageHandler messageHandler;
  private long pingInterval = PING_INTERVAL_DEFAULT;
  private URI reconnectURL;
  private URI proxyURI;
  private int retransmits;

  public SlackTransport ( LogService log, BundleContext context, BotFactory botFactory, URI slackURL ) {
    this.log = log;
    this.botFactory = botFactory;
    this.context = context;
    api = new API ( slackURL, log );
    messageHandler = new SlackMessageHandler ();
    clientManager = ClientManager.createClient ();
    clientManager.getScheduledExecutorService ()
        .scheduleAtFixedRate ( new RetransmitHandler (), 0, 10, TimeUnit.SECONDS );
    clientManager.getScheduledExecutorService ().scheduleAtFixedRate ( new PingHandler (), 0, 100, TimeUnit.MILLISECONDS );
    clientManager.getScheduledExecutorService ().scheduleAtFixedRate ( new MarkHandler (), 0, 30, TimeUnit.SECONDS );
  }

  protected void registerService () {
    if ( registration != null ) {
      unregisterService ();
    }
    registration = context.registerService ( Transport.class, this, buildServiceProperties () );
    providerRegistration =
        context.registerService ( StatusInfoProvider.class, new StatusProvider (), new Hashtable<String, Object> () );
  }

  protected Dictionary<String, Object> buildServiceProperties () {
    Dictionary<String, Object> prop = new Hashtable<> ();
    prop.put ( "service.pid", pid );
    if ( team != null ) {
      prop.put ( "service.description", format ( "Slack Connection to {0}", team.getName () ) );
    }
    return prop;
  }

  protected void unregisterService () {
    if ( registration != null ) {
      registration.unregister ();
    }
    if ( providerRegistration != null ) {
      providerRegistration.unregister ();
    }
  }

  protected void updateService () {
    if ( registration != null ) {
      registration.setProperties ( buildServiceProperties () );
    }
  }

  public String getName () {
    return "Slack";
  }

  public void connect () {
    try {
      URI url;
      if ( proxyURI != null ) {
        clientManager.getProperties ().put ( ClientProperties.PROXY_URI, proxyURI.toString () );
      } else {
        clientManager.getProperties ().remove ( ClientProperties.PROXY_URI );
      }
      if ( reconnectURL == null ) {
        api.setProxy ( proxyURI );
        RTMStart startData = api.doRTMStart ();
        team = startData.getTeam ();
        self = startData.getSelf ();
        for ( SlackUser slackUser : startData.getUsers () ) {
          users.addUser ( slackUser );
        }
        for ( SlackChannel channel : startData.getChannels () ) {
          channels.addChannel ( channel );
        }
        for ( SlackIM im : startData.getIms () ) {
          channels.addChannel ( im );
        }
        url = startData.getUrl ();
      } else {
        url = reconnectURL;
        reconnectURL = null;
      }
      log.log ( LogService.LOG_INFO, format ( "Connecting to SlackTeam {0} URL is {1}", team.getName (), url ) );
      open = true;
      final ClientEndpointConfig endpointConfig = ClientEndpointConfig.Builder.create ()
          .decoders ( Collections.<Class<? extends Decoder>>singletonList ( SlackWSDecoder.class ) )
          .encoders ( Collections.<Class<? extends Encoder>>singletonList ( SlackWSEncoder.class ) )
          .build ();
      endpointConfig.getUserProperties ().put ( "transport", this );
      endpointConfig.getUserProperties ().put ( "log", log );
      session = clientManager.connectToServer ( new SlackWSEndpoint (), endpointConfig, url );
      registerService ();
    } catch ( IOException | DeploymentException e ) {
      log.log ( LogService.LOG_ERROR, null, e );
      reconnect ();
    }
  }

  public void disconnect () {
    open = false;
    if ( session != null ) {
      try {
        session.close ( new CloseReason ( CloseReason.CloseCodes.GOING_AWAY, "Disconnect" ) );
      } catch ( IOException e ) {
        log.log ( LogService.LOG_ERROR, "disconnect", e );
      }
    }
  }

  private void reconnect () {
    if ( open ) {
      clientManager.getScheduledExecutorService ().schedule ( new Runnable () {
        @Override
        public void run () {
          connect ();
        }
      }, 10, TimeUnit.SECONDS );
    }
  }

  protected void incPacketCount ( Map<String, Integer> packets, String type ) {
    synchronized ( packets ) {
      if ( !packets.containsKey ( type ) ) {
        packets.put ( type, 0 );
      }
      packets.put ( type, packets.get ( type ) + 1 );
    }
  }

  protected void sendMessage ( BaseEvent msg ) {
    incPacketCount ( outPackets, msg.getType () );
    msg.setId ( nextId () );
    retryTimers.add ( new RetryTimer ( new Date ( System.currentTimeMillis () + 10000 ), msg.getId () ) );
    pendingMessages.put ( msg.getId (), msg );
    try {
      if ( open ) {
        session.getBasicRemote ().sendObject ( msg );
      }
    } catch ( IOException | EncodeException e ) {
      log.log ( LogService.LOG_WARNING, "Error sending message", e );
    }
  }

  @Override
  public void reply ( au.id.vanlaatum.botter.api.Message message, String text ) {
    Message msg = new Message ();
    msg.setText ( text );
    if ( message.getChannel () instanceof AbstractSlackMessageChannel ) {
      msg.setChannel ( message.getChannel ().getID () );
    }
    sendMessage ( msg );
  }

  @Override
  public void error ( au.id.vanlaatum.botter.api.Message message, String text ) {
    reply ( message, text );
  }

  @Override
  public boolean isMyName ( String text ) {
    if ( text.startsWith ( "@" ) ) {
      text = text.substring ( 1 );
    }
    return self.getName ().equalsIgnoreCase ( text );
  }

  private synchronized Integer nextId () {
    if ( msgId < 0 ) {
      msgId = 0;
    }
    return msgId++;
  }

  public void updated ( Dictionary<String, ?> dictionary ) throws ConfigurationException {
    log.log ( LogService.LOG_INFO, format ( "Updated config {0}", dictionary ) );
    if ( dictionary.get ( PING_INTERVAL ) != null ) {
      pingInterval = (Long) dictionary.get ( PING_INTERVAL );
    } else {
      pingInterval = PING_INTERVAL_DEFAULT;
    }

    final String s = (String) dictionary.get ( PROXY_URI );
    if ( s != null && !s.trim ().isEmpty () ) {
      try {
        proxyURI = new URI ( s );
      } catch ( URISyntaxException e ) {
        throw new ConfigurationException ( PROXY_URI, "invalid url", e );
      }
    } else {
      proxyURI = null;
    }

    pid = (String) dictionary.get ( "service.pid" );
    if ( api.setToken ( (String) dictionary.get ( "token" ) ) || !open ) {
      clientManager.getExecutorService ().submit ( new Runnable () {
        @Override
        public void run () {
          connect ();
        }
      } );
    } else {
      updateService ();
    }
  }

  private SlackMessageDTO buildSlackMessageDTO ( Message packet )
      throws UserNotFoundException, ChannelNotFoundException {
    final SlackMessageDTO dto = new SlackMessageDTO ( this );
    dto.setText ( messagePreProcessor.convertText ( packet.getText (), dto ) );
    dto.setUser ( new SlackUserDTO ( users.getUser ( packet.getUser () ) ) );
    dto.setChannel ( channels.get ( packet.getChannel () ) );
    return dto;
  }

  Users getUsers () {
    return users;
  }

  private synchronized void ackPacket ( Integer id ) {
    if ( id != null ) {
      pendingMessages.remove ( id );
    }
  }

  void shutdown () {
    if ( clientManager != null ) {
      clientManager.shutdown ();
    }
  }

  private class RetryTimer implements Delayed {

    private Date expires;
    private Integer id;

    RetryTimer ( Date expires, Integer id ) {
      this.expires = expires;
      this.id = id;
    }

    public Integer getId () {
      return id;
    }

    @Override
    public long getDelay ( TimeUnit unit ) {
      return unit.convert ( expires.getTime () - new Date ().getTime (), TimeUnit.MILLISECONDS );
    }

    @Override
    public int compareTo ( Delayed o ) {
      if ( o instanceof RetryTimer ) {
        return expires.compareTo ( ( (RetryTimer) o ).expires );
      } else {
        return 0;
      }
    }
  }

  private class SlackWSEndpoint extends Endpoint {

    @Override
    public void onOpen ( Session session, EndpointConfig config ) {
      log.log ( LogService.LOG_INFO, "Session opened for " + team.getName () );
      session.addMessageHandler ( messageHandler );
    }

    @Override
    public void onClose ( Session session, CloseReason closeReason ) {
      log.log ( LogService.LOG_INFO, "Session closed " + closeReason + " for " + team.getName () );
      reconnect ();
    }

    @Override
    public void onError ( Session session, Throwable thr ) {
      if ( thr instanceof DecodeException && thr.getCause () instanceof JsonMappingException ) {
        log.log ( LogService.LOG_WARNING, format ( "Unknown or invalid packet on {0}: {1}", team.getName (),
            thr.getCause ().getMessage () ) );
      } else {
        log.log ( LogService.LOG_ERROR, format ( "Websocket error on {0}", team.getName () ), thr );
      }
    }
  }

  private class SlackMessageHandler implements MessageHandler.Whole<BasePacket> {
    @Override
    public void onMessage ( final BasePacket packet ) {
      try {
        incPacketCount ( inPackets,
            packet instanceof BaseEvent ? ( (BaseEvent) packet ).getType () : packet.getClass ().getSimpleName () );
        ackPacket ( packet.getReplyTo () );
        lastPacket = new Date ();
        log.log ( LogService.LOG_INFO, format ( "Message {0}: {1}", packet, packet.getRaw () ) );
        if ( packet instanceof Message && !Objects.equals ( ( (Message) packet ).getUser (), self.getId () ) ) {
          channels.get ( ( (Message) packet ).getChannel () ).setMark ( ( (Message) packet ).getTs () );
          botFactory.processMessage ( buildSlackMessageDTO ( (Message) packet ) );
        } else if ( packet instanceof UserChange ) {
          users.updateUser ( ( (UserChange) packet ).getUser () );
        } else if ( packet instanceof TeamJoin ) {
          users.addUser ( ( (TeamJoin) packet ).getUser () );
        } else if ( packet instanceof Pong ) {
          rtt = System.currentTimeMillis () - ( (Pong) packet ).getTime ().getTime ();
        } else if ( packet instanceof ReconnectURL ) {
          reconnectURL = ( (ReconnectURL) packet ).getUrl ();
        } else if ( packet instanceof Marked ) {
          channels.get ( ( (Marked) packet ).getChannel () ).updateMark ( ( (Marked) packet ).getTs () );
        }
      } catch ( Exception e ) {
        log.log ( LogService.LOG_WARNING, "Exception while processing packet", e );
      }
    }
  }


  private class StatusProvider implements StatusInfoProvider {

    @Override
    public String getName () {
      return SlackTransport.this.getName ();
    }

    @Override
    public String getStatus () {
      StringBuilder buffer = new StringBuilder ();
      buffer.append ( "In Packets:\n" );
      for ( Map.Entry<String, Integer> entry : inPackets.entrySet () ) {
        buffer.append ( "\t" ).append ( entry.getKey () ).append ( "=" ).append ( entry.getValue () ).append ( "\n" );
      }
      buffer.append ( "Out Packets:\n" );
      for ( Map.Entry<String, Integer> entry : outPackets.entrySet () ) {
        buffer.append ( "\t" ).append ( entry.getKey () ).append ( "=" ).append ( entry.getValue () ).append ( "\n" );
      }
      return format ( "{0} {1}: {2} pending messages\n\trtt = {3}\n\tlast packet = {4}\n\tretransmits = {5}\n\t" +
              "channels = {6}\n{7}",
          getName (), team.getName (), pendingMessages.size (), rtt, lastPacket, retransmits, channels.size (),
          buffer.toString ().trim () );
    }
  }

  private class RetransmitHandler implements Runnable {
    @Override
    public synchronized void run () {
      if ( open ) {
        RetryTimer timer;
        while ( ( timer = retryTimers.poll () ) != null ) {
          final BaseEvent message = pendingMessages.get ( timer.getId () );
          if ( message != null ) {
            log.log ( LogService.LOG_DEBUG, format ( "Retransmitting {0}", message.getId () ) );
            retransmits++;
            sendMessage ( message );
          }
        }
      }
    }
  }

  private class PingHandler implements Runnable {

    @Override
    public void run () {
      if ( open ) {
        final long now = System.currentTimeMillis ();
        if ( lastPing.getTime () <= now - pingInterval ) {
          sendMessage ( new Ping () );
          lastPing = new Date ( now );
        }

        if ( lastPacket.getTime () < now - pingInterval * 3 ) {
          disconnect ();
          connect ();
        }
      }
    }
  }

  private class MarkHandler implements Runnable {

    @Override
    public void run () {
      if ( open ) {
        for ( AbstractSlackMessageChannel channel : channels.pendingMark () ) {
          try {
            if ( channel instanceof SlackMessageChannel ) {
              api.doChannelMark ( channel.getID (), channel.getMark () );
            } else if ( channel instanceof SlackMessageChannelIM ) {
              api.doIMMark ( channel.getID (), channel.getMark () );
            }
            channel.setCallMark ( false );
          } catch ( IOException e ) {
            log.log ( LogService.LOG_WARNING, "Failed to mark channel " + channel.getID (), e );
          }
        }
      }
    }
  }
}
