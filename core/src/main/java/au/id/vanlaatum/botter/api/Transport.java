package au.id.vanlaatum.botter.api;

public interface Transport {
  String getName ();

  void connect ();

  void disconnect ();

  void reply ( Message message, String text );

  void error ( Message message, String text );

  void annotate ( Message message, String text );

  boolean isMyName ( String text );
}
