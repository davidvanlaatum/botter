package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.transport.slack.modal.rtm.Ack;
import au.id.vanlaatum.botter.transport.slack.modal.rtm.BaseEvent;
import au.id.vanlaatum.botter.transport.slack.modal.rtm.BasePacket;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

@SuppressWarnings ( "WeakerAccess" )
public class SlackWSDecoder implements Decoder.Text<BasePacket> {
  private final ObjectMapper mapper = new ObjectMapper ();
  private SlackTransport transport;

  @Override
  public BasePacket decode ( String json ) throws DecodeException {
    try {
      BasePacket rt;
      final JsonNode tree = mapper.readTree ( json );
      if ( tree.has ( "type" ) ) {
        final Class<? extends BaseEvent> classForPacket = BaseEvent.getClassForPacket ( tree );
        rt = mapper.convertValue ( tree, classForPacket );
      } else if ( tree.has ( "ok" ) ) {
        rt = mapper.convertValue ( tree, Ack.class );
      } else {
        throw new DecodeException ( json, "Unknown packet type! " );
      }
      rt.setRaw ( json );
      rt.setTree ( tree );
      return rt;
    } catch ( IOException e ) {
      throw new DecodeException ( json, null, e );
    }
  }

  @Override
  public boolean willDecode ( String s ) {
    return true;
  }

  @Override
  public void init ( EndpointConfig endpointConfig ) {
    mapper.disable ( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES );
    transport = (SlackTransport) endpointConfig.getUserProperties ().get ( "transport" );
  }

  @Override
  public void destroy () {

  }
}
