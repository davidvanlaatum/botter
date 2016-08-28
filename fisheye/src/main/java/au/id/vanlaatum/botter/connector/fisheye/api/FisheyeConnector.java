package au.id.vanlaatum.botter.connector.fisheye.api;

import java.io.IOException;
import java.net.URI;

public interface FisheyeConnector {
  boolean isEnabled ();

  ChangeSet getChangeSet ( String repo, String id ) throws IOException, RemoteCallFailedException;

  boolean urlMatches ( URI uri );

  URI removePrefix ( URI uri );

  String getName ();
}
