package au.id.vanlaatum.botter.api;

public class CacheLookupException extends Exception {
  public CacheLookupException () {
  }

  public CacheLookupException ( String message ) {
    super ( message );
  }

  public CacheLookupException ( String message, Throwable cause ) {
    super ( message, cause );
  }

  public CacheLookupException ( Throwable cause ) {
    super ( cause );
  }
}
