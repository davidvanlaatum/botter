package au.id.vanlaatum.botter.transport.slack.modal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;

public class Self {
  private String id;
  private String name;
  private Map<String, Object> prefs;
  private Date created;
  private String manualPresence;

  public String getId () {
    return id;
  }

  public void setId ( String id ) {
    this.id = id;
  }

  public String getName () {
    return name;
  }

  public void setName ( String name ) {
    this.name = name;
  }

  public Map<String, Object> getPrefs () {
    return prefs;
  }

  public void setPrefs ( Map<String, Object> prefs ) {
    this.prefs = prefs;
  }

  public Date getCreated () {
    return created;
  }

  public void setCreated ( Date created ) {
    this.created = created;
  }

  @JsonProperty ( "manual_presence" )
  public String getManualPresence () {
    return manualPresence;
  }

  public void setManualPresence ( String manualPresence ) {
    this.manualPresence = manualPresence;
  }
}
