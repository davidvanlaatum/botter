package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.api.BotFactory;
import au.id.vanlaatum.botter.api.StatusInfoProvider;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.api.User;
import au.id.vanlaatum.botter.transport.slack.modal.RTMStart;
import au.id.vanlaatum.botter.transport.slack.modal.Self;
import au.id.vanlaatum.botter.transport.slack.modal.SlackChannel;
import au.id.vanlaatum.botter.transport.slack.modal.SlackIM;
import au.id.vanlaatum.botter.transport.slack.modal.SlackTeam;
import au.id.vanlaatum.botter.transport.slack.modal.SlackUser;
import au.id.vanlaatum.botter.transport.slack.modal.rtm.Attachment;
import au.id.vanlaatum.botter.transport.slack.modal.rtm.BaseEvent;
import au.id.vanlaatum.botter.transport.slack.modal.rtm.BasePacket;
import au.id.vanlaatum.botter.transport.slack.modal.rtm.Hello;
import au.id.vanlaatum.botter.transport.slack.modal.rtm.Marked;
import au.id.vanlaatum.botter.transport.slack.modal.rtm.Message;
import au.id.vanlaatum.botter.transport.slack.modal.rtm.Ping;
import au.id.vanlaatum.botter.transport.slack.modal.rtm.Pong;
import au.id.vanlaatum.botter.transport.slack.modal.rtm.ReconnectURL;
import au.id.vanlaatum.botter.transport.slack.modal.rtm.TeamJoin;
import au.id.vanlaatum.botter.transport.slack.modal.rtm.UserChange;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.commons.lang3.ObjectUtils;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
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
  public static final String ENABLED = "enabled";
  public static final String TOKEN = "token";
  static final String PING_INTERVAL = "ping.interval";
  static final long PING_INTERVAL_DEFAULT = 10000;
  static final String PROXY_URI = "proxy.url";
  private final Users users = new Users ();
  private final Channels channels = new Channels ();
  private final Map<String, Integer> inPackets = new TreeMap<> ();
  private final Map<String, Integer> outPackets = new TreeMap<> ();
  private BundleContext context;
  private LogService log;
  private BotFactory botFactory;
  private API api;
  private String pid;
  private ClientManager clientManager;
  private Session session;
  private boolean open;
  private boolean enabled;
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
  private Date lastConnectAttempt = new Date ();
  private Date connectedSince;
  private long rtt;
  private SlackMessageHandler messageHandler;
  private long pingInterval = PING_INTERVAL_DEFAULT;
  private URI reconnectURL;
  private URI proxyURI;
  private int retransmits;

  public SlackTransport () {
    messageHandler = new SlackMessageHandler ();
  }

  public SlackTransport setClientManager ( ClientManager clientManager ) {
    if ( this.clientManager != null ) {
      clientManager.shutdown ();
    }
    this.clientManager = clientManager;
    clientManager.getScheduledExecutorService ()
        .scheduleAtFixedRate ( new RetransmitHandler (), 0, 10, TimeUnit.SECONDS );
    clientManager.getScheduledExecutorService ()
        .scheduleAtFixedRate ( new PingHandler (), 0, 100, TimeUnit.MILLISECONDS );
    clientManager.getScheduledExecutorService ().scheduleAtFixedRate ( new MarkHandler (), 0, 30, TimeUnit.SECONDS );
    return this;
  }

  public SlackTransport setApi ( API api ) {
    this.api = api;
    return this;
  }

  public SlackTransport setLog ( LogService log ) {
    this.log = log;
    messagePreProcessor.setLog ( log );
    return this;
  }

  public SlackTransport setContext ( BundleContext context ) {
    this.context = context;
    return this;
  }

  public SlackTransport setBotFactory ( BotFactory botFactory ) {
    this.botFactory = botFactory;
    return this;
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
      prop.put ( Constants.SERVICE_DESCRIPTION, format ( "Slack Connection to {0}", team.getName () ) );
    }
    return prop;
  }

  protected void unregisterService () {
    if ( registration != null ) {
      registration.unregister ();
      registration = null;
    }
    if ( providerRegistration != null ) {
      providerRegistration.unregister ();
      providerRegistration = null;
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
      if ( enabled ) {
        lastConnectAttempt = new Date ();
        if ( proxyURI != null ) {
          clientManager.getProperties ().put ( ClientProperties.PROXY_URI, proxyURI.toString () );
        } else {
          clientManager.getProperties ().remove ( ClientProperties.PROXY_URI );
        }
        URI url = getConnectURI ();
        log.log ( LogService.LOG_INFO, format ( "Connecting to SlackTeam {0} URL is {1}", team.getName (), url ) );
        open = true;
        final ClientEndpointConfig endpointConfig = ClientEndpointConfig.Builder.create ()
            .decoders ( Collections.<Class<? extends Decoder>>singletonList ( SlackWSDecoder.class ) )
            .encoders ( Collections.<Class<? extends Encoder>>singletonList ( SlackWSEncoder.class ) )
            .build ();
        endpointConfig.getUserProperties ().put ( "transport", this );
        endpointConfig.getUserProperties ().put ( "log", log );
        session = clientManager.connectToServer ( messageHandler, endpointConfig, url );
        registerService ();
      }
    } catch ( Exception e ) {
      log.log ( LogService.LOG_ERROR, null, e );
      reconnect ();
    }
  }

  private URI getConnectURI () throws IOException {
    URI url;
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
    return url;
  }

  public void disconnect () {
    open = false;
    connectedSince = null;
    unregisterService ();
    if ( session != null ) {
      try {
        session.close ( new CloseReason ( CloseReason.CloseCodes.GOING_AWAY, "Disconnect" ) );
      } catch ( Exception e ) {
        log.log ( LogService.LOG_ERROR, "disconnect", e );
      }
    }
  }

  private void reconnect () {
    disconnect ();
    if ( enabled ) {
      clientManager.getScheduledExecutorService ().schedule ( new Runnable () {
        @Override
        public void run () {
          connect ();
        }
      }, 10, TimeUnit.SECONDS );
    }
  }

  private void incPacketCount ( Map<String, Integer> packets, String type ) {
    synchronized ( packets ) {
      if ( !packets.containsKey ( type ) ) {
        packets.put ( type, 0 );
      }
      packets.put ( type, packets.get ( type ) + 1 );
    }
  }

  private void sendMessage ( BaseEvent msg ) {
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
  public void annotate ( au.id.vanlaatum.botter.api.Message message, String text ) {
    boolean sent = false;
    if ( message instanceof SlackMessageDTO ) {
      try {
        final au.id.vanlaatum.botter.transport.slack.modal.BasePacket basePacket =
            api.doChatUpdate ( ( (SlackMessageDTO) message ).getOriginalMessage (),
                Collections.singletonList ( new Attachment ( text ) ) );
        if ( basePacket.getOk () ) {
          sent = true;
        }
      } catch ( IOException e ) {
        log.log ( LogService.LOG_WARNING, "Error annotating message", e );
      }
    }
    if ( !sent ) {
      reply ( message, text );
    }
  }

  @Override
  public boolean isMyName ( String text ) {
    String name = text;
    if ( name.startsWith ( "@" ) ) {
      name = name.substring ( 1 );
    }
    return self.getName ().equalsIgnoreCase ( name );
  }

  @Override
  public User getUser ( String userName ) throws UserNotFoundException {
    return new SlackUserDTO ( users.getUserByUsername ( userName ), this );
  }

  @Override
  public User getUserByUniqID ( String userId ) throws UserNotFoundException {
    if ( userId.startsWith ( pid ) ) {
      return new SlackUserDTO ( users.getUser ( userId.substring ( pid.length () + 1 ) ), this );
    } else {
      throw new UserNotFoundException ();
    }
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

    if ( dictionary.get ( ENABLED ) != null ) {
      enabled = (Boolean) dictionary.get ( ENABLED );
    }

    pid = (String) dictionary.get ( Constants.SERVICE_PID );
    if ( api.setToken ( (String) dictionary.get ( TOKEN ) ) || !open ) {
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
    dto.setUser ( new SlackUserDTO ( users.getUser ( packet.getUser () ), this ) );
    dto.setChannel ( channels.get ( packet.getChannel () ) );
    dto.setOriginalMessage ( packet );
    return dto;
  }

  Users getUsers () {
    return users;
  }

  private synchronized void ackPacket ( BasePacket packet ) {
    if ( packet.getReplyTo () != null ) {
      final BaseEvent event = pendingMessages.remove ( packet.getReplyTo () );
      if ( event instanceof Message ) {
        try {
          channels.get ( ( (Message) event ).getChannel () ).setMark ( packet.getTs () );
        } catch ( ChannelNotFoundException e ) {
          log.log ( LogService.LOG_WARNING, "Channel not found", e );
        }
      }
    }
  }

  void shutdown () {
    if ( clientManager != null ) {
      clientManager.shutdown ();
    }
  }

  public String getPID () {
    return pid;
  }

  private static class RetryTimer implements Delayed {

    private Date expires;
    private Integer id;

    RetryTimer ( Date expires, Integer id ) {
      this.expires = ObjectUtils.clone ( expires );
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

  private class SlackMessageHandler extends Endpoint implements MessageHandler.Whole<BasePacket> {
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

    @Override
    public void onMessage ( final BasePacket packet ) {
      try {
        incPacketCount ( inPackets,
            packet instanceof BaseEvent ? ( (BaseEvent) packet ).getType () : packet.getClass ().getSimpleName () );
        ackPacket ( packet );
        lastPacket = new Date ();
        log.log ( LogService.LOG_DEBUG, format ( "Message {0}: {1}", packet, packet.getRaw () ) );
        if ( packet instanceof Message && !Objects.equals ( ( (Message) packet ).getUser (), self.getId () ) ) {
          channels.get ( ( (Message) packet ).getChannel () ).setMark ( packet.getTs () );
          botFactory.processMessage ( buildSlackMessageDTO ( (Message) packet ) );
        } else if ( packet instanceof Hello ) {
          connectedSince = new Date ();
        } else if ( packet instanceof UserChange ) {
          users.updateUser ( ( (UserChange) packet ).getUser () );
        } else if ( packet instanceof TeamJoin ) {
          users.addUser ( ( (TeamJoin) packet ).getUser () );
        } else if ( packet instanceof Pong ) {
          rtt = System.currentTimeMillis () - ( (Pong) packet ).getTime ().getTime ();
        } else if ( packet instanceof ReconnectURL ) {
          reconnectURL = ( (ReconnectURL) packet ).getUrl ();
        } else if ( packet instanceof Marked ) {
          channels.get ( ( (Marked) packet ).getChannel () ).updateMark ( packet.getTs () );
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
      return format ( "{0} {1}: {2} pending messages\n" +
              "\trtt = {3}\n" +
              "\tlast packet = {4}\n" +
              "\tretransmits = {5}\n" +
              "\tchannels = {6}\n" +
              "\tconnected since = {7}\n" +
              "\tlast connect attempt = {8}\n" +
              "{9}",
          getName (), team.getName (), pendingMessages.size (), rtt, lastPacket, retransmits, channels.size (),
          connectedSince, lastConnectAttempt,
          buffer.toString ().trim () );
    }
  }

  private class RetransmitHandler implements Runnable {
    @Override
    public synchronized void run () {
      if ( enabled ) {
        RetryTimer timer;
        while ( ( timer = retryTimers.poll () ) != null ) {
          final BaseEvent message = pendingMessages.get ( timer.getId () );
          if ( message != null ) {
            log.log ( LogService.LOG_DEBUG, format ( "Retransmitting {0}", message.getId () ) );
            retransmits++;
            sendMessage ( message );
          }
        }
        if ( session != null && !session.isOpen () &&
            lastConnectAttempt.getTime () < System.currentTimeMillis () - 60000 ) {
          reconnect ();
        }
      }
    }
  }

  private class PingHandler implements Runnable {

    @Override
    public void run () {
      if ( enabled ) {
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
      if ( enabled ) {
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
