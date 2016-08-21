package au.id.vanlaatum.botter.api;

public interface CommandProcessor {
  String getName ();

  String getHelp ();

  boolean process ( Command command );
}
