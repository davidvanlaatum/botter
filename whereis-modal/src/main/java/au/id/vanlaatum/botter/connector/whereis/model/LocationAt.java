package au.id.vanlaatum.botter.connector.whereis.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

@Entity
@NamedQueries ( {
    @NamedQuery ( name = "LocationAt.findByUser", query = "SELECT l FROM LocationAt l WHERE l.user = :user AND l.startDate >= :start" ),
    @NamedQuery ( name = "LocationAt.findCurrentByUser",
        query = "SELECT l FROM LocationAt l WHERE l.user = :user AND l.startDate <= :now AND l.endDate > :now ORDER BY l.id DESC" ),
    @NamedQuery ( name = "LocationAt.findOverlappingByUser",
        query = "SELECT l FROM LocationAt l WHERE l.user = :user AND l.startDate = :start AND l.endDate = :end" )
} )
@Table ( name = "locations", uniqueConstraints = @UniqueConstraint ( columnNames = { "user_id", "startDate", "endDate" } ) )
public class LocationAt {
  @Id
  @GeneratedValue ( strategy = GenerationType.IDENTITY )
  private Integer id;

  @ManyToOne ( optional = false )
  private User user;

  @Column ( nullable = false )
  private Date startDate;

  @Column ( nullable = false )
  private Date endDate;

  @Column ( nullable = false )
  private String description;

  public Integer getId () {
    return id;
  }

  public LocationAt setId ( Integer id ) {
    this.id = id;
    return this;
  }

  public User getUser () {
    return user;
  }

  public LocationAt setUser ( User user ) {
    this.user = user;
    return this;
  }

  public Date getStartDate () {
    return startDate;
  }

  public LocationAt setStartDate ( Date start ) {
    this.startDate = start;
    return this;
  }

  public Date getEndDate () {
    return endDate;
  }

  public LocationAt setEndDate ( Date end ) {
    this.endDate = end;
    return this;
  }

  public String getDescription () {
    return description;
  }

  public LocationAt setDescription ( String description ) {
    this.description = description;
    return this;
  }
}
