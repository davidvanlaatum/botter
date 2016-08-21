package au.id.vanlaatum.botter.transport.slack;

public class UserNotFoundException extends Exception {
  public UserNotFoundException ( String msg ) {
    super ( msg );
  }
}
