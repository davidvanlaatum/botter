package au.id.vanlaatum.botter.transport.slack.Modal.RTM;

import au.id.vanlaatum.botter.transport.slack.Modal.SlackTimeStamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings ( { "WeakerAccess", "unused" } )
public class Message extends BaseEvent {
  private static Map<String, Class<? extends BaseEvent>> typeMap = new TreeMap<> ();

  static {
    typeMap.put ( "channel_join", MessageChannelJoin.class );
    typeMap.put ( "bot_message", MessageBot.class );
    typeMap.put ( "message_changed", MessageChanged.class );
  }

  private String subtype;
  private String text;
  private String user;
  private String channel;
  private String team;
  private Boolean Ephemeral;
  private String username;
  private Map<String, Object> msg;
  private SlackTimeStamp eventTs;
  private List<Attachment> attachments;
  private Boolean hidden;

  public Message () {
    setType ( "message" );
  }

  public static Class<? extends BaseEvent> getClassForPacket ( JsonNode tree ) {
    Class<? extends BaseEvent> rt = Message.class;

    if ( tree.has ( "subtype" ) ) {
      String subtype = tree.get ( "subtype" ).asText ();
      rt = typeMap.get ( subtype );
      if ( rt == null ) {
        rt = Message.class;
      }
    }

    return rt;
  }

  public String getSubtype () {
    return subtype;
  }

  public void setSubtype ( String subtype ) {
    this.subtype = subtype;
  }

  public String getTeam () {
    return team;
  }

  public void setTeam ( String team ) {
    this.team = team;
  }

  public String getText () {
    return text;
  }

  public void setText ( String text ) {
    this.text = text;
  }

  public String getUser () {
    return user;
  }

  public void setUser ( String user ) {
    this.user = user;
  }

  public String getChannel () {
    return channel;
  }

  public void setChannel ( String channel ) {
    this.channel = channel;
  }

  @JsonProperty ( "is_ephemeral" )
  public Boolean isEphemeral () {
    return Ephemeral;
  }

  public Message setEphemeral ( Boolean ephemeral ) {
    Ephemeral = ephemeral;
    return this;
  }

  public String getUsername () {
    return username;
  }

  public Message setUsername ( String username ) {
    this.username = username;
    return this;
  }

  public Map<String, Object> getMsg () {
    return msg;
  }

  public Message setMsg ( Map<String, Object> msg ) {
    this.msg = msg;
    return this;
  }

  @JsonProperty ( "event_ts" )
  public SlackTimeStamp getEventTs () {
    return eventTs;
  }

  public Message setEventTs ( SlackTimeStamp eventTs ) {
    this.eventTs = eventTs;
    return this;
  }

  public List<Attachment> getAttachments () {
    return attachments;
  }

  public Message setAttachments ( List<Attachment> attachments ) {
    this.attachments = attachments;
    return this;
  }

  public Boolean getHidden () {
    return hidden;
  }

  public Message setHidden ( Boolean hidden ) {
    this.hidden = hidden;
    return this;
  }
}
