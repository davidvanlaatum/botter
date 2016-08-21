package au.id.vanlaatum.botter.api;

import java.util.List;

public interface Command {
  Message getMessage ();

  String getCommandText ();

  List<String> getCommandParts ();

  User getUser ();

  Transport getTransport ();

  void reply ( String message );

  void error ( String message );
}
