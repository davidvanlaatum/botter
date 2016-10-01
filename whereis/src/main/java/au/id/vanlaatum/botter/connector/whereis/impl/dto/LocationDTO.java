package au.id.vanlaatum.botter.connector.whereis.impl.dto;

import au.id.vanlaatum.botter.connector.whereis.api.Location;
import au.id.vanlaatum.botter.connector.whereis.api.User;
import au.id.vanlaatum.botter.connector.whereis.impl.model.LocationAt;

import java.util.Date;

public class LocationDTO implements Location {
  private String description;
  private Date start;
  private Date end;
  private User user;

  public LocationDTO ( LocationAt at ) {
    description = at.getDescription ();
    start = at.getStart ();
    end = at.getEnd ();
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
  public Date getStart () {
    return start;
  }

  @Override
  public Date getEnd () {
    return end;
  }
}
