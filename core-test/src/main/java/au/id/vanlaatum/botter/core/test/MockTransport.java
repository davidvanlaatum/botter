package au.id.vanlaatum.botter.core.test;

import au.id.vanlaatum.botter.api.Message;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.api.User;

import java.util.HashMap;
import java.util.Map;

public class MockTransport implements Transport {
  private final Map<String, User> usersByUsername = new HashMap<> ();
  private final Map<String, User> usersByUniqID = new HashMap<> ();

  @Override
  public String getName () {
    return "MockTransport";
  }

  @Override
  public void connect () {

  }

  @Override
  public void disconnect () {

  }

  @Override
  public void reply ( Message message, String text ) {

  }

  @Override
  public void error ( Message message, String text ) {

  }

  @Override
  public void annotate ( Message message, String text ) {

  }

  @Override
  public boolean isMyName ( String text ) {
    return false;
  }

  @Override
  public User getUser ( String userName ) throws UserNotFoundException {
    User rt = usersByUsername.get ( userName );
    if ( rt == null ) {
      throw new UserNotFoundException ( userName );
    }
    return rt;
  }

  @Override
  public User getUserByUniqID ( String userId ) throws UserNotFoundException {
    User rt = usersByUniqID.get ( userId );
    if ( rt == null ) {
      throw new UserNotFoundException ( userId );
    }
    return rt;
  }

  void addUser ( String userName ) {
    User user = new MockUser ( userName );
    usersByUsername.put ( user.getName (), user );
    usersByUniqID.put ( user.getUniqID (), user );
  }
}
