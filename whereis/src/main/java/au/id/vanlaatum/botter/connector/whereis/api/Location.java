package au.id.vanlaatum.botter.connector.whereis.api;

import java.util.Date;

public interface Location {
  User getUser ();

  String getDescription ();

  Date getStart ();

  Date getEnd ();
}
