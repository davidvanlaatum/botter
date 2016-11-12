package au.id.vanlaatum.botter.transport.slack.modal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;

public class RTMStartTest {
  @Test
  public void deserialise () throws Exception {
    ObjectMapper mapper = new ObjectMapper ();
    try ( final InputStream stream = getClass ().getResourceAsStream ( "/RTMStart.json" ) ) {
      final RTMStart start = mapper.readValue ( stream, RTMStart.class );
    }
  }
}
