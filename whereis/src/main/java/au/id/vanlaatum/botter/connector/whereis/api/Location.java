package au.id.vanlaatum.botter.connector.whereis.api;

import org.joda.time.DateTime;

public interface Location {
  User getUser ();

  String getDescription ();

  DateTime getStart ();

  DateTime getEnd ();
}
