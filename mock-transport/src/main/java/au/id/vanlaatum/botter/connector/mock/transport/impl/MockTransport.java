package au.id.vanlaatum.botter.connector.mock.transport.impl;

import au.id.vanlaatum.botter.api.Message;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.api.User;

class MockTransport implements Transport {
  private final MockTransportConfiguratorImpl mockTransportConfigurator;

  MockTransport ( MockTransportConfiguratorImpl mockTransportConfigurator ) {
    this.mockTransportConfigurator = mockTransportConfigurator;
  }

  @Override
  public String getName () {
    return "Mock Transport";
  }

  @Override
  public void connect () {

  }

  @Override
  public void disconnect () {

  }

  @Override
  public void reply ( Message message, String text ) {
    if ( message instanceof MessageAssertImpl ) {
      ( (MessageAssertImpl) message ).reply ( text );
    } else {
      throw new RuntimeException ();
    }
  }

  @Override
  public void error ( Message message, String text ) {
    if ( message instanceof MessageAssertImpl ) {
      ( (MessageAssertImpl) message ).error ( text );
    } else {
      throw new RuntimeException ();
    }
  }

  @Override
  public void annotate ( Message message, String text ) {
    if ( message instanceof MessageAssertImpl ) {
      ( (MessageAssertImpl) message ).annotate ( text );
    } else {
      throw new RuntimeException ();
    }
  }

  @Override
  public boolean isMyName ( String text ) {
    return false;
  }

  @Override
  public User getUser ( String userName ) throws UserNotFoundException {
    return mockTransportConfigurator.getUserById ( userName );
  }

  @Override
  public User getUserByUniqID ( String userId ) throws UserNotFoundException {
    return mockTransportConfigurator.getUserById ( userId );
  }
}
