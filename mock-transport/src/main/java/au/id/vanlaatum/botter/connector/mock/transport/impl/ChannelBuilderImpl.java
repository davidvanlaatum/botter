package au.id.vanlaatum.botter.connector.mock.transport.impl;

import au.id.vanlaatum.botter.connector.mock.transport.api.ChannelBuilder;

class ChannelBuilderImpl implements ChannelBuilder {
  private final MockTransportConfiguratorImpl mockTransportConfigurator;
  private final String id;
  private boolean direct;

  ChannelBuilderImpl ( MockTransportConfiguratorImpl mockTransportConfigurator, String id ) {
    this.mockTransportConfigurator = mockTransportConfigurator;
    this.id = id;
  }

  @Override
  public void build () {
    mockTransportConfigurator.addChannel ( new MockChannel ( direct, id, id ) );
  }
}
