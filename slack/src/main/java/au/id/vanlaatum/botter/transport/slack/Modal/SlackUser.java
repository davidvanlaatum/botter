package au.id.vanlaatum.botter.transport.slack.Modal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SlackUser {
  private String id;
  private String teamId;
  private String name;
  private Boolean deleted;
  private String status;
  private String color;
  private String realName;
  private String tz;
  private String tzLabel;
  private Integer tzOffset;
  private SlackUserProfile profile;
  private Boolean admin;
  private Boolean owner;
  private Boolean primaryOwner;
  private Boolean restricted;
  private Boolean ultraRestricted;
  private Boolean bot;
  private String presence;

  @JsonProperty ( "is_admin" )
  public Boolean isAdmin () {
    return admin;
  }

  public void setAdmin ( Boolean admin ) {
    this.admin = admin;
  }

  @JsonProperty ( "is_owner" )
  public Boolean isOwner () {
    return owner;
  }

  public void setOwner ( Boolean owner ) {
    this.owner = owner;
  }

  @JsonProperty ( "is_primary_owner" )
  public Boolean getPrimaryOwner () {
    return primaryOwner;
  }

  public void setPrimaryOwner ( Boolean primaryOwner ) {
    this.primaryOwner = primaryOwner;
  }

  @JsonProperty ( "is_restricted" )
  public Boolean isRestricted () {
    return restricted;
  }

  public void setRestricted ( Boolean restricted ) {
    this.restricted = restricted;
  }

  @JsonProperty ( "is_ultra_restricted" )
  public Boolean isUltraRestricted () {
    return ultraRestricted;
  }

  public void setUltraRestricted ( Boolean ultraRestricted ) {
    this.ultraRestricted = ultraRestricted;
  }

  @JsonProperty ( "is_bot" )
  public Boolean isBot () {
    return bot;
  }

  public void setBot ( Boolean bot ) {
    this.bot = bot;
  }

  public String getPresence () {
    return presence;
  }

  public void setPresence ( String presence ) {
    this.presence = presence;
  }

  @JsonProperty ( "team_id" )
  public String getTeamId () {
    return teamId;
  }

  public void setTeamId ( String teamId ) {
    this.teamId = teamId;
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

  public Boolean getDeleted () {
    return deleted;
  }

  public void setDeleted ( Boolean deleted ) {
    this.deleted = deleted;
  }

  public String getStatus () {
    return status;
  }

  public void setStatus ( String status ) {
    this.status = status;
  }

  public String getColor () {
    return color;
  }

  public void setColor ( String color ) {
    this.color = color;
  }

  @JsonProperty ( "real_name" )
  public String getRealName () {
    return realName;
  }

  public void setRealName ( String realName ) {
    this.realName = realName;
  }

  public String getTz () {
    return tz;
  }

  public void setTz ( String tz ) {
    this.tz = tz;
  }

  @JsonProperty ( "tz_label" )
  public String getTzLabel () {
    return tzLabel;
  }

  public void setTzLabel ( String tzLabel ) {
    this.tzLabel = tzLabel;
  }

  @JsonProperty ( "tz_offset" )
  public Integer getTzOffset () {
    return tzOffset;
  }

  public void setTzOffset ( Integer tzOffset ) {
    this.tzOffset = tzOffset;
  }

  public SlackUserProfile getProfile () {
    return profile;
  }

  public void setProfile ( SlackUserProfile profile ) {
    this.profile = profile;
  }
}
