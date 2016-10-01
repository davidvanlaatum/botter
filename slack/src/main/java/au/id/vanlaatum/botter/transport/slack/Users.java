package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.transport.slack.Modal.SlackUser;

import java.util.Map;
import java.util.Objects;
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

  public SlackUser getUser ( String id ) throws Transport.UserNotFoundException {
    final SlackUser user = users.get ( id );
    if ( user == null ) {
      throw new Transport.UserNotFoundException ( "User with id " + id + " not found" );
    }
    return user;
  }

  public SlackUser getUserByUsername ( String id ) throws Transport.UserNotFoundException {
    SlackUser rt = null;
    for ( SlackUser slackUser : users.values () ) {
      if ( Objects.equals ( slackUser.getName (), id ) ) {
        rt = slackUser;
        break;
      }
    }
    if ( rt == null ) {
      throw new Transport.UserNotFoundException ( "User " + id + " not found" );
    }
    return rt;
  }
}
