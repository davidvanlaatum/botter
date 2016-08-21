package au.id.vanlaatum.botter.transport.slack.Modal;

import java.net.URI;
import java.util.Map;

public class Bot {
  private String id;
  private Boolean deleted;
  private String name;
  private Map<String,URI> icons;

  public Boolean getDeleted () {
    return deleted;
  }

  public void setDeleted ( Boolean deleted ) {
    this.deleted = deleted;
  }

  public String getName () {
    return name;
  }

  public void setName ( String name ) {
    this.name = name;
  }

  public Map<String, URI> getIcons () {
    return icons;
  }

  public void setIcons ( Map<String, URI> icons ) {
    this.icons = icons;
  }

  public String getId () {
    return id;
  }

  public void setId ( String id ) {
    this.id = id;
  }
}
