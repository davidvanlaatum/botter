package au.id.vanlaatum.botter.connector.mock.transport.impl;

import au.id.vanlaatum.botter.connector.mock.transport.api.UserBuilder;

class UserBuilderImpl implements UserBuilder {
  private final MockTransportConfiguratorImpl mockTransportConfigurator;
  private final String userId;

  UserBuilderImpl ( MockTransportConfiguratorImpl mockTransportConfigurator, String userId ) {
    this.mockTransportConfigurator = mockTransportConfigurator;
    this.userId = userId;
  }

  @Override
  public void build () {
    final MockUser user = new MockUser ( userId );
    mockTransportConfigurator.addUser ( user );
  }
}
