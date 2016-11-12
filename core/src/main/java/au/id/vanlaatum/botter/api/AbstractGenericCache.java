package au.id.vanlaatum.botter.api;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class AbstractGenericCache<Key, Value> implements GenericCache<Key, Value> {
  private final Map<Key, BaseCacheObject> data = new TreeMap<> ();
  private final ReferenceQueue<Value> referenceQueue = new ReferenceQueue<> ();
  private final DelayQueue<BaseCacheObject> cleanupQueue = new DelayQueue<> ();
  private long cacheTime = 300000;

  @Override
  public synchronized Value lookup ( Key key ) {
    final BaseCacheObject object = data.get ( key );
    if ( object != null && object.getDelay ( TimeUnit.MILLISECONDS ) > 0 ) {
      return object.get ();
    }
    return null;
  }

  @Override
  public Value lookup ( Key key, Callable<Value> method ) throws CacheLookupException {
    final BaseCacheObject object = data.get ( key );
    if ( object != null && object.getDelay ( TimeUnit.MILLISECONDS ) > 0 ) {
      return object.get ();
    } else {
      try {
        Value rt = method.call ();
        add ( key, rt );
        return rt;
      } catch ( CacheLookupException ex ) {
        throw ex;
      } catch ( Exception ex ) {
        throw new CacheLookupException ( ex );
      }
    }
  }

  @Override
  public synchronized void cleanup () {
    BaseCacheObject object = cleanupQueue.poll ();
    while ( object != null ) {
      object.enqueue ();
      object = cleanupQueue.poll ();
    }
    Reference<? extends Value> value = referenceQueue.poll ();
    while ( value != null ) {
      if ( value instanceof AbstractGenericCache.BaseCacheObject ) {
        @SuppressWarnings ( "unchecked" )
        final BaseCacheObject cacheObject = (AbstractGenericCache<Key, Value>.BaseCacheObject) value;
        object = data.remove ( cacheObject.getKey () );
        if ( object != cacheObject ) {
          data.put ( object.getKey (), object );
        }
      }
      value = referenceQueue.poll ();
    }
  }

  @Override
  public int size () {
    return data.size ();
  }

  protected void setCacheTime ( long time ) {
    cacheTime = time;
  }

  @Override
  public synchronized void add ( Key key, Value value ) {
    final BaseCacheObject cacheObject = new BaseCacheObject ( key, value );
    data.put ( key, cacheObject );
    cleanupQueue.add ( cacheObject );
  }

  private class BaseCacheObject extends WeakReference<Value> implements CacheObject<Value>, Delayed {

    private final Key key;
    private long start;

    BaseCacheObject ( Key key, Value referent ) {
      super ( referent, referenceQueue );
      this.key = key;
      this.start = System.currentTimeMillis ();
    }

    Key getKey () {
      return key;
    }

    @Override
    public long getDelay ( TimeUnit unit ) {
      return unit.convert ( ( start + cacheTime ) - System.currentTimeMillis (), TimeUnit.MILLISECONDS );
    }

    @Override
    public int compareTo ( Delayed o ) {
      return o.getDelay ( TimeUnit.MILLISECONDS ) > getDelay ( TimeUnit.MILLISECONDS ) ? 1 : -1;
    }
  }
}
