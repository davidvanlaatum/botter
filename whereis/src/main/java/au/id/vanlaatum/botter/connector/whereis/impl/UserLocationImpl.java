package au.id.vanlaatum.botter.connector.whereis.impl;

import au.id.vanlaatum.botter.connector.whereis.api.Location;
import au.id.vanlaatum.botter.connector.whereis.api.UserLocation;
import au.id.vanlaatum.botter.connector.whereis.impl.dto.LocationDTO;
import au.id.vanlaatum.botter.connector.whereis.impl.model.LocationAt;
import au.id.vanlaatum.botter.connector.whereis.impl.model.User;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.osgi.service.log.LogService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@OsgiServiceProvider ( classes = UserLocation.class )
@Singleton
@Transactional
public class UserLocationImpl implements UserLocation {
  @PersistenceUnit ( unitName = "whereis" )
  private EntityManagerFactory emf;
  @Inject
  @Named ( "logService" )
  private LogService log;

  @Override
  public List<Location> getUpcomingLocationsForUser ( String id, Date start ) {
    EntityManager em = emf.createEntityManager ();
    List<Location> rt = new ArrayList<> ();
    try {
      User user = getOrCreateUser ( id, em );
      final TypedQuery<LocationAt> query = em.createNamedQuery ( "LocationAt.findByUser", LocationAt.class );
      query.setParameter ( "user", user );
      query.setParameter ( "start", start );
      final List<LocationAt> resultList = query.getResultList ();
      for ( LocationAt at : resultList ) {
        rt.add ( new LocationDTO ( at ) );
      }
    } finally {
      em.close ();
    }
    return rt;
  }

  @Override
  public Location getCurrentLocationForUser ( String id, Date now ) {
    EntityManager em = emf.createEntityManager ();
    Location rt = null;
    try {
      User user = getOrCreateUser ( id, em );
      final TypedQuery<LocationAt> query = em.createNamedQuery ( "LocationAt.findCurrentByUser", LocationAt.class );
      query.setParameter ( "user", user );
      query.setParameter ( "now", now );
      final LocationAt resultList = query.getSingleResult ();
      rt = new LocationDTO ( resultList );
    } catch ( NoResultException ex ) {
      log.log ( LogService.LOG_DEBUG, "Not found", ex );
    } finally {
      em.close ();
    }
    return rt;
  }

  User getOrCreateUser ( String id, EntityManager em ) {
    User rt;
    try {
      final TypedQuery<User> findByID = em.createNamedQuery ( "findByID", User.class );
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
  public void addLocationForUser ( String id, Date from, Date to, String description ) {
    EntityManager em = emf.createEntityManager ();
    try {
      User user = getOrCreateUser ( id, em );
      LocationAt locationAt = new LocationAt ();
      locationAt.setUser ( user );
      locationAt.setStart ( from );
      locationAt.setEnd ( to );
      locationAt.setDescription ( description );
      em.persist ( locationAt );
    } finally {
      em.close ();
    }
  }
}
