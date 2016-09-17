package au.id.vanlaatum.botter.api;

import java.util.TimeZone;

public interface User {

  boolean isBot ();

  boolean isAdmin ();

  boolean isUser ();

  String getName ();

  String getUniqID ();

  TimeZone getTimezone();
}
