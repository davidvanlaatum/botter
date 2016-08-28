package au.id.vanlaatum.botter.api;

public interface User {

  boolean isBot ();

  boolean isAdmin ();

  boolean isUser ();

  String getName ();
}
