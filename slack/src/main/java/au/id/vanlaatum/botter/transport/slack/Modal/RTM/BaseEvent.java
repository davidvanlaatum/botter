package au.id.vanlaatum.botter.transport.slack.Modal.RTM;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;
import java.util.TreeMap;

public class BaseEvent extends BasePacket {
  private static final Map<String, Class<? extends BaseEvent>> typeMap = new TreeMap<> ();

  static {
    typeMap.put ( "hello", Hello.class );
    typeMap.put ( "presence_change", PresenceChange.class );
    typeMap.put ( "user_typing", UserTyping.class );
    typeMap.put ( "reconnect_url", ReconnectURL.class );
    typeMap.put ( "team_join", TeamJoin.class );
    typeMap.put ( "user_change", UserChange.class );
    typeMap.put ( "pong", Pong.class );
    typeMap.put ( "channel_marked", ChannelMarked.class );
    typeMap.put ( "im_marked", IMMarked.class );
  }

  private String type;

  public static Class<? extends BaseEvent> getClassForPacket ( JsonNode tree ) {
    Class<? extends BaseEvent> rt;
    final String type = tree.get ( "type" ).asText ();
    switch ( type ) {
      case "message":
        rt = Message.getClassForPacket ( tree );
        break;
      default:
        rt = typeMap.get ( type );
        if ( rt == null ) {
          rt = BaseEvent.class;
        }
    }
    return rt;
  }

  public String getType () {
    return type;
  }

  public void setType ( String type ) {
    this.type = type;
  }

  @Override
  public String toString () {
    return new ReflectionToStringBuilder ( this, ToStringStyle.SHORT_PREFIX_STYLE ).setExcludeFieldNames ( "raw", "tree" )
        .toString ();
  }
}
