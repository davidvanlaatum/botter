package au.id.vanlaatum.botter.connector.whereis.impl;

import au.id.vanlaatum.botter.connector.whereis.api.Location;
import au.id.vanlaatum.botter.connector.whereis.api.UserLocation;
import au.id.vanlaatum.botter.connector.whereis.impl.dto.LocationDTO;
import au.id.vanlaatum.botter.connector.whereis.model.LocationAt;
import au.id.vanlaatum.botter.connector.whereis.model.User;
import org.joda.time.DateTime;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.osgi.service.log.LogService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Objects.requireNonNull;

@OsgiServiceProvider ( classes = UserLocation.class )
@Singleton
@Transactional ( value = Transactional.TxType.REQUIRED )
public class UserLocationImpl implements UserLocation {
  @PersistenceContext ( unitName = "botter-whereis" )
  private EntityManager em;
  @Inject
  @Named ( "logService" )
  private LogService log;

  @Override
  public List<Location> getUpcomingLocationsForUser ( String id, Date start ) {
    List<Location> rt = new ArrayList<> ();
    User user = getOrCreateUser ( id );
    final TypedQuery<LocationAt> query = em.createNamedQuery ( "LocationAt.findByUser", LocationAt.class );
    query.setParameter ( "user", user );
    query.setParameter ( "start", start );
    final List<LocationAt> resultList = query.getResultList ();
    for ( LocationAt at : resultList ) {
      rt.add ( new LocationDTO ( at ) );
    }
    return rt;
  }

  @Override
  public Location getCurrentLocationForUser ( String id, Date now ) {
    Location rt = null;
    try {
      User user = getOrCreateUser ( id );
      final TypedQuery<LocationAt> query = em.createNamedQuery ( "LocationAt.findCurrentByUser", LocationAt.class );
      query.setParameter ( "user", user );
      query.setParameter ( "now", now );
      query.setMaxResults ( 1 );
      final List<LocationAt> resultList = query.getResultList ();
      if ( !resultList.isEmpty () ) {
        rt = new LocationDTO ( resultList.get ( 0 ) );
      }
    } catch ( NoResultException ex ) {
      log.log ( LogService.LOG_DEBUG, "Not found", ex );
    }
    return rt;
  }

  private User getOrCreateUser ( String id ) {
    User rt;
    try {
      final TypedQuery<User> findByID = em.createNamedQuery ( "User.findByID", User.class );
      findByID.setParameter ( "userId", id );
      rt = findByID.getSingleResult ();
    } catch ( NoResultException ex ) {
      rt = new User ();
      rt.setUserId ( id );
      em.persist ( rt );
    }
    return rt;
  }

  @Override
  public Location addLocationForUser ( String id, DateTime from, DateTime to, String description ) {
    requireNonNull ( from, "from null" );
    requireNonNull ( to, "to null" );
    requireNonNull ( id, "id null" );
    requireNonNull ( description, "description null" );
    if ( to.isBefore ( from ) ) {
      throw new RuntimeException ( "to mast be after from " + from + " >= " + to );
    }
    User user = getOrCreateUser ( id );
    final TypedQuery<LocationAt> query = em.createNamedQuery ( "LocationAt.findOverlappingByUser", LocationAt.class );
    query.setParameter ( "start", from.toDate () );
    query.setParameter ( "end", to.toDate () );
    query.setParameter ( "user", user );
    LocationAt locationAt = new LocationAt ();
    try {
      locationAt = query.getSingleResult ();
    } catch ( NoResultException ignore ) {

    }
    locationAt.setUser ( user );
    locationAt.setStartDate ( from.toDate () );
    locationAt.setEndDate ( to.toDate () );
    locationAt.setDescription ( description );
    em.persist ( locationAt );
    return new LocationDTO ( locationAt );
  }
}
