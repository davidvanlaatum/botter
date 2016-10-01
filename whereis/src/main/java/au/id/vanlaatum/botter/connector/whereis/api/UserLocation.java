package au.id.vanlaatum.botter.connector.whereis.api;

import java.util.Date;
import java.util.List;

public interface UserLocation {
  List<Location> getUpcomingLocationsForUser ( String id, Date start );

  Location getCurrentLocationForUser ( String id, Date now );

  void addLocationForUser ( String id, Date from, Date to, String description );
}
