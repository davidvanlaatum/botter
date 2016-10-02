package au.id.vanlaatum.botter.connector.whereis.impl.dto;

import au.id.vanlaatum.botter.connector.whereis.api.Location;
import au.id.vanlaatum.botter.connector.whereis.api.User;
import au.id.vanlaatum.botter.connector.whereis.impl.model.LocationAt;
import org.joda.time.DateTime;

public class LocationDTO implements Location {
  private String description;
  private DateTime start;
  private DateTime end;
  private User user;

  public LocationDTO ( LocationAt at ) {
    description = at.getDescription ();
    start = new DateTime ( at.getStart () );
    end = new DateTime ( at.getEnd () );
    user = new UserDTO ( at.getUser () );
  }

  @Override
  public User getUser () {
    return user;
  }

  @Override
  public String getDescription () {
    return description;
  }

  @Override
  public DateTime getStart () {
    return start;
  }

  @Override
  public DateTime getEnd () {
    return end;
  }
}
