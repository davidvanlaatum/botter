package au.id.vanlaatum.botter.connector.mock.transport.impl;

import au.id.vanlaatum.botter.api.BotFactory;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.connector.mock.transport.api.ChannelBuilder;
import au.id.vanlaatum.botter.connector.mock.transport.api.MessageBuilder;
import au.id.vanlaatum.botter.connector.mock.transport.api.MockTransportConfigurator;
import au.id.vanlaatum.botter.connector.mock.transport.api.UserBuilder;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@OsgiServiceProvider ( classes = MockTransportConfigurator.class )
@Named ( "MockTransportConfigurator" )
public class MockTransportConfiguratorImpl implements MockTransportConfigurator {
  private final MockTransport transport = new MockTransport ( this );
  private final List<MockUser> users = new ArrayList<> ();
  private final Map<String, MockChannel> channels = new TreeMap<> ();
  @OsgiService ( required = true )
  @Inject
  private BotFactory factory;
  @Inject
  @Named ( "blueprintBundleContext" )
  private BundleContext context;
  private ServiceRegistration<Transport> registration;

  @Override
  public UserBuilder addUser ( String userId ) {
    return new UserBuilderImpl ( this, userId );
  }

  @Override
  public ChannelBuilder addChannel ( String id ) {
    return new ChannelBuilderImpl ( this, id );
  }

  @Override
  public MessageBuilder injectMessage ( String message ) {
    return new MessageBuilderImpl ( this, message );
  }

  @Override
  public void start () {
    registration = context.registerService ( Transport.class, transport, new Hashtable<String, Object> () );
  }

  @Override
  public void stop () {
    if ( registration != null ) {
      registration.unregister ();
      registration = null;
    }
  }

  @Override
  public BotFactory getBotFactory () {
    return factory;
  }

  @Override
  public Transport getTransport () {
    return transport;
  }

  @Override
  public MockUser getUserById ( String userId ) throws Transport.UserNotFoundException {
    MockUser user = null;

    for ( MockUser mockUser : users ) {
      if ( Objects.equals ( mockUser.getUniqID (), userId ) ) {
        user = mockUser;
        break;
      }
    }

    if ( user == null ) {
      throw new Transport.UserNotFoundException ( userId );
    }

    return user;
  }

  void addChannel ( MockChannel mockChannel ) {
    channels.put ( mockChannel.getID (), mockChannel );
  }

  @Override
  public MockChannel getChannel ( String channelId ) {
    final MockChannel channel = channels.get ( channelId );
    if ( channel == null ) {
      throw new RuntimeException ( "Channel " + channelId + " not found" );
    }
    return channel;
  }

  void addUser ( MockUser user ) {
    users.add ( user );
  }
}
