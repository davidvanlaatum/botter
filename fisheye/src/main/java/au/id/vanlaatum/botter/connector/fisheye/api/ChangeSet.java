package au.id.vanlaatum.botter.connector.fisheye.api;

import java.util.Date;
import java.util.List;

public interface ChangeSet {
  String getID ();

  String getRepositoryName ();

  String getAuthor ();

  String getComment ();

  Date getDate ();

  List<File> getFiles ();

  interface File {
    String getPath ();
  }
}
