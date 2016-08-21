package au.id.vanlaatum.botter.transport.slack.Modal.RTM;

import au.id.vanlaatum.botter.transport.slack.Modal.SlackTimeStamp;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings ( { "WeakerAccess", "unused" } )
public class Message extends BaseEvent {
  private static Map<String, Class<? extends BaseEvent>> typeMap = new TreeMap<> ();

  static {
    typeMap.put ( "channel_join", MessageChannelJoin.class );
  }

  private String subtype;
  private String text;
  private String user;
  private String channel;
  private SlackTimeStamp ts;
  private String team;

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

  public SlackTimeStamp getTs () {
    return ts;
  }

  public void setTs ( SlackTimeStamp ts ) {
    this.ts = ts;
  }
}
