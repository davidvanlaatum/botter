package au.id.vanlaatum.botter.connector.whereis.impl.model;

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
    @NamedQuery ( name = "LocationAt.findByUser", query = "SELECT l FROM LocationAt l WHERE l.user = :user AND l.start >= :start" ),
    @NamedQuery ( name = "LocationAt.findCurrentByUser",
        query = "SELECT l FROM LocationAt l WHERE l.user = :user AND l.start <= :now AND l.end > :now ORDER BY l.id DESC" ),
    @NamedQuery ( name = "LocationAt.findOverlappingByUser",
        query = "SELECT l FROM LocationAt l WHERE l.user = :user AND (l.start BETWEEN :start AND :end) OR (l.end BETWEEN :start AND :end) OR (:start BETWEEN l.start AND l.end) OR (:end BETWEEN l.start AND l.end)" )
} )
@Table ( uniqueConstraints = @UniqueConstraint ( columnNames = { "user_id", "start", "end" } ) )
public class LocationAt {
  @Id
  @GeneratedValue ( strategy = GenerationType.IDENTITY )
  private Integer id;

  @ManyToOne ( optional = false )
  private User user;

  @Column ( nullable = false )
  private Date start;

  @Column ( nullable = false )
  private Date end;

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

  public Date getStart () {
    return start;
  }

  public LocationAt setStart ( Date start ) {
    this.start = start;
    return this;
  }

  public Date getEnd () {
    return end;
  }

  public LocationAt setEnd ( Date end ) {
    this.end = end;
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
