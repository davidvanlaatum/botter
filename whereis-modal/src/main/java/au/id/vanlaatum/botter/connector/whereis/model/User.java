package au.id.vanlaatum.botter.connector.whereis.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@NamedQueries (
    @NamedQuery ( name = "User.findByID", query = "SELECT u FROM User u WHERE u.userId = :userId" )
)
@Table ( name = "users", uniqueConstraints = @UniqueConstraint ( columnNames = "userId" ) )
public class User {
  @Id
  @GeneratedValue ( strategy = GenerationType.IDENTITY )
  private Integer id;
  @Column ( nullable = false )
  private String userId;

  public Integer getId () {
    return id;
  }

  public User setId ( Integer id ) {
    this.id = id;
    return this;
  }

  public String getUserId () {
    return userId;
  }

  public User setUserId ( String userId ) {
    this.userId = userId;
    return this;
  }
}
