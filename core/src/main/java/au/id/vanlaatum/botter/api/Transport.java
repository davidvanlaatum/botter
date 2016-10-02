package au.id.vanlaatum.botter.api;

public interface Transport {
  String getName ();

  void connect ();

  void disconnect ();

  void reply ( Message message, String text );

  void error ( Message message, String text );

  void annotate ( Message message, String text );

  boolean isMyName ( String text );

  User getUser ( String userName ) throws UserNotFoundException;

  User getUserByUniqID ( String userId ) throws UserNotFoundException;

  class UserNotFoundException extends Exception {
    public UserNotFoundException () {
    }

    public UserNotFoundException ( String message ) {
      super ( message );
    }

    public UserNotFoundException ( String message, Throwable cause ) {
      super ( message, cause );
    }

    public UserNotFoundException ( Throwable cause ) {
      super ( cause );
    }
  }
}
