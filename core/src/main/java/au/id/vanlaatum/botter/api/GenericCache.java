package au.id.vanlaatum.botter.api;

import java.util.concurrent.Callable;

public interface GenericCache<Key, Value> {
  Value lookup ( Key key );

  Value lookup ( Key key, Callable<Value> method ) throws Exception;

  void add ( Key key, Value value );

  void cleanup ();

  int size ();

  String getName();

  interface CacheObject<Value> {
  }
}
