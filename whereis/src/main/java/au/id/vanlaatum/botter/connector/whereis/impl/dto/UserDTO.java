package au.id.vanlaatum.botter.connector.whereis.impl.dto;

import au.id.vanlaatum.botter.connector.whereis.api.User;

public class UserDTO implements User {
  private final String uniqId;

  UserDTO ( au.id.vanlaatum.botter.connector.whereis.impl.model.User user ) {
    uniqId = user.getUserId ();
  }

  @Override
  public String getUniqID () {
    return uniqId;
  }
}
