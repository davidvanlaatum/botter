package au.id.vanlaatum.botter.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AbstractGenericCacheTest {
  @Test
  public void test () throws Exception {
    TestCache cache = new TestCache ();
    cache.add ( "123", "456" );
    assertEquals ( "456", cache.lookup ( "123" ) );
    assertEquals ( 1, cache.size () );
    cache.cleanup ();
    assertEquals ( 1, cache.size () );
    Thread.sleep ( 500 );
    cache.cleanup ();
    assertEquals ( 0, cache.size () );
    assertNull ( cache.lookup ( "123" ) );
  }

  private class TestCache extends AbstractGenericCache<String, String> {
    TestCache () {
      setCacheTime ( 500 );
    }

    @Override
    public String getName () {
      return null;
    }
  }
}
