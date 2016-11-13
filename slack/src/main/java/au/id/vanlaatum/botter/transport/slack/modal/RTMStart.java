package au.id.vanlaatum.botter.transport.slack.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.ObjectUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SuppressWarnings ( { "WeakerAccess", "unused" } )
public class RTMStart extends BasePacket {
  private URI url;
  private SlackTeam team;
  private Self self;
  private List<SlackUser> users;
  private SlackTimeStamp latestEventTS;
  private List<SlackChannel> channels;
  private List<Group> groups;
  private List<SlackIM> ims;
  private Date cacheTS;
  private SubTeams subTeams;
  private DnD dnd;
  private String cacheVersion;
  private String cacheTSVersion;
  private List<Bot> bots;

  public List<Bot> getBots () {
    return Collections.unmodifiableList ( bots );
  }

  public void setBots ( List<Bot> bots ) {
    this.bots = new ArrayList<> ( bots );
  }

  @JsonProperty ( "cache_version" )
  public String getCacheVersion () {
    return cacheVersion;
  }

  public void setCacheVersion ( String cacheVersion ) {
    this.cacheVersion = cacheVersion;
  }

  @JsonProperty ( "cache_ts_version" )
  public String getCacheTSVersion () {
    return cacheTSVersion;
  }

  public void setCacheTSVersion ( String cacheTSVersion ) {
    this.cacheTSVersion = cacheTSVersion;
  }

  public List<Group> getGroups () {
    return Collections.unmodifiableList ( groups );
  }

  public void setGroups ( List<Group> groups ) {
    this.groups = new ArrayList<> ( groups );
  }

  public List<SlackIM> getIms () {
    return Collections.unmodifiableList ( ims );
  }

  public void setIms ( List<SlackIM> ims ) {
    this.ims = new ArrayList<> ( ims );
  }

  @JsonProperty ( "cache_ts" )
  public Date getCacheTS () {
    return ObjectUtils.clone ( cacheTS );
  }

  public void setCacheTS ( Date cacheTS ) {
    this.cacheTS = ObjectUtils.clone ( cacheTS );
  }

  @JsonProperty ( "subteams" )
  public SubTeams getSubTeams () {
    return subTeams;
  }

  public void setSubTeams ( SubTeams subTeams ) {
    this.subTeams = subTeams;
  }

  public DnD getDnd () {
    return dnd;
  }

  public void setDnd ( DnD dnd ) {
    this.dnd = dnd;
  }

  public List<SlackChannel> getChannels () {
    return Collections.unmodifiableList ( channels );
  }

  public void setChannels ( List<SlackChannel> channels ) {
    this.channels = new ArrayList<> ( channels );
  }

  @JsonProperty ( "latest_event_ts" )
  public SlackTimeStamp getLatestEventTS () {
    return latestEventTS;
  }

  public void setLatestEventTS ( SlackTimeStamp latestEventTS ) {
    this.latestEventTS = latestEventTS;
  }

  public Self getSelf () {
    return self;
  }

  public void setSelf ( Self self ) {
    this.self = self;
  }

  public URI getUrl () {
    return url;
  }

  public void setUrl ( URI url ) {
    this.url = url;
  }

  public SlackTeam getTeam () {
    return team;
  }

  public void setTeam ( SlackTeam team ) {
    this.team = team;
  }

  public List<SlackUser> getUsers () {
    return Collections.unmodifiableList ( users );
  }

  public void setUsers ( List<SlackUser> users ) {
    this.users = new ArrayList<> ( users );
  }

}
