package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.transport.slack.Modal.SlackUser;

import java.util.Map;
import java.util.TreeMap;

public class Users {
  private Map<String, SlackUser> users = new TreeMap<> ();

  public void addUser ( SlackUser user ) {
    users.put ( user.getId (), user );
  }

  public void updateUser ( SlackUser user ) {
    final SlackUser slackUser = users.get ( user.getId () );
    if ( slackUser == null ) {
      addUser ( user );
    } else {

    }
  }

  public SlackUser getUser ( String id ) throws UserNotFoundException {
    final SlackUser user = users.get ( id );
    if ( user == null ) {
      throw new UserNotFoundException ( "User with id " + id + " not found" );
    }
    return user;
  }
}
