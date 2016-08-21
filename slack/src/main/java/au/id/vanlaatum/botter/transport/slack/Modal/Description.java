package au.id.vanlaatum.botter.transport.slack.Modal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Description {
  private String value;
  private String creator;
  private Date lastSet;

  public String getValue () {
    return value;
  }

  public void setValue ( String value ) {
    this.value = value;
  }

  public String getCreator () {
    return creator;
  }

  public void setCreator ( String creator ) {
    this.creator = creator;
  }

  @JsonProperty("last_set")
  public Date getLastSet () {
    return lastSet;
  }

  public void setLastSet ( Date lastSet ) {
    this.lastSet = lastSet;
  }
}
