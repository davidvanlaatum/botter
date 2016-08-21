package au.id.vanlaatum.botter.transport.slack.Modal;


import au.id.vanlaatum.botter.transport.slack.Modal.RTM.Message;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class SlackChannel {
  private String id;
  private String name;
  private Boolean isChannel;
  private Date created;
  private String creator;
  private Boolean isArchived;
  private Boolean isGeneral;
  private Boolean hasPins;
  private Boolean isMember;
  private SlackTimeStamp lastRead;
  private Message latest;
  private Integer unreadCount;
  private Integer unreadCountDisplay;
  private List<String> members;
  private Description topic;
  private Description purpose;

  public List<String> getMembers () {
    return members;
  }

  public void setMembers ( List<String> members ) {
    this.members = members;
  }

  public Description getTopic () {
    return topic;
  }

  public void setTopic ( Description topic ) {
    this.topic = topic;
  }

  public Description getPurpose () {
    return purpose;
  }

  public void setPurpose ( Description purpose ) {
    this.purpose = purpose;
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

  public Message getLatest () {
    return latest;
  }

  public void setLatest ( Message latest ) {
    this.latest = latest;
  }

  @JsonProperty ( "last_read" )
  public SlackTimeStamp getLastRead () {
    return lastRead;
  }

  public void setLastRead ( SlackTimeStamp lastRead ) {
    this.lastRead = lastRead;
  }

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

  @JsonProperty ( "is_channel" )
  public Boolean getChannel () {
    return isChannel;
  }

  public void setChannel ( Boolean channel ) {
    isChannel = channel;
  }

  public Date getCreated () {
    return created;
  }

  public void setCreated ( Date created ) {
    this.created = created;
  }

  public String getCreator () {
    return creator;
  }

  public void setCreator ( String creator ) {
    this.creator = creator;
  }

  @JsonProperty ( "is_archived" )
  public Boolean getArchived () {
    return isArchived;
  }

  public void setArchived ( Boolean archived ) {
    isArchived = archived;
  }

  @JsonProperty ( "is_general" )
  public Boolean getGeneral () {
    return isGeneral;
  }

  public void setGeneral ( Boolean general ) {
    isGeneral = general;
  }

  @JsonProperty ( "has_pins" )
  public Boolean getHasPins () {
    return hasPins;
  }

  public void setHasPins ( Boolean hasPins ) {
    this.hasPins = hasPins;
  }

  @JsonProperty ( "is_member" )
  public Boolean getMember () {
    return isMember;
  }

  public void setMember ( Boolean member ) {
    isMember = member;
  }
}
