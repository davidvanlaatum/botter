package au.id.vanlaatum.botter.transport.slack.modal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class SlackIM {
  private String id;
  private String user;
  private Date created;
  private Boolean isIM;
  private Boolean isOrgShared;
  private Boolean hasPins;
  private SlackTimeStamp lastRead;
  private Object latest;
  private Integer unreadCount;
  private Integer unreadCountDisplay;
  private Boolean isOpen;

  public String getId () {
    return id;
  }

  public void setId ( String id ) {
    this.id = id;
  }

  public String getUser () {
    return user;
  }

  public void setUser ( String user ) {
    this.user = user;
  }

  public Date getCreated () {
    return created;
  }

  public void setCreated ( Date created ) {
    this.created = created;
  }

  @JsonProperty ( "is_im" )
  public Boolean getIM () {
    return isIM;
  }

  public void setIM ( Boolean IM ) {
    isIM = IM;
  }

  @JsonProperty ( "is_org_shared" )
  public Boolean getOrgShared () {
    return isOrgShared;
  }

  public void setOrgShared ( Boolean orgShared ) {
    isOrgShared = orgShared;
  }

  @JsonProperty ( "has_pins" )
  public Boolean getHasPins () {
    return hasPins;
  }

  public void setHasPins ( Boolean hasPins ) {
    this.hasPins = hasPins;
  }

  @JsonProperty ( "last_read" )
  public SlackTimeStamp getLastRead () {
    return lastRead;
  }

  public void setLastRead ( SlackTimeStamp lastRead ) {
    this.lastRead = lastRead;
  }

  public Object getLatest () {
    return latest;
  }

  public void setLatest ( Object latest ) {
    this.latest = latest;
  }

  @JsonProperty ( "unread_count" )
  public Integer getUnreadCount () {
    return unreadCount;
  }

  public void setUnreadCount ( Integer unreadCount ) {
    this.unreadCount = unreadCount;
  }

  @JsonProperty ( "unread_count_display" )
  public Integer getUnreadCountDisplay () {
    return unreadCountDisplay;
  }

  public void setUnreadCountDisplay ( Integer unreadCountDisplay ) {
    this.unreadCountDisplay = unreadCountDisplay;
  }

  @JsonProperty ( "is_open" )
  public Boolean getOpen () {
    return isOpen;
  }

  public void setOpen ( Boolean open ) {
    isOpen = open;
  }
}
