package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.transport.slack.modal.rtm.BaseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.osgi.service.log.LogService;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import static java.text.MessageFormat.format;


@SuppressWarnings ( "WeakerAccess" )
public class SlackWSEncoder implements Encoder.Text<BaseEvent> {
  private final ObjectMapper mapper = new ObjectMapper ();
  private SlackTransport transport;
  private LogService log;

  @Override
  public String encode ( BaseEvent baseEvent ) throws EncodeException {
    try {
      String json = mapper.writeValueAsString ( baseEvent );
      log.log ( LogService.LOG_DEBUG, format ( "Sending {0}: {1}", baseEvent, json ) );
      return json;
    } catch ( JsonProcessingException e ) {
      throw new EncodeException ( baseEvent, null, e );
    }
  }

  @Override
  public void init ( EndpointConfig endpointConfig ) {
    mapper.disable ( SerializationFeature.WRITE_NULL_MAP_VALUES );
    transport = (SlackTransport) endpointConfig.getUserProperties ().get ( "transport" );
    log = (LogService) endpointConfig.getUserProperties ().get ( "log" );
  }

  @Override
  public void destroy () {

  }
}
