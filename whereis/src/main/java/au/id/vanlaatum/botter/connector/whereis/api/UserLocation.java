package au.id.vanlaatum.botter.connector.whereis.api;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

public interface UserLocation {
  List<Location> getUpcomingLocationsForUser ( String id, Date start );

  Location getCurrentLocationForUser ( String id, Date now );

  Location addLocationForUser ( String id, DateTime from, DateTime to, String description );
}
