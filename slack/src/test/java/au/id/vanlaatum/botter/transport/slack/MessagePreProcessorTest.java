package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.transport.slack.modal.SlackUser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class MessagePreProcessorTest {
  @Test
  public void convertText () throws Exception {
    final SlackTransport transport = mock ( SlackTransport.class );
    Users users = new Users ();
    when ( transport.getUsers () ).thenReturn ( users );
    MessagePreProcessor processor = new MessagePreProcessor ( transport );
    final SlackUser user = new SlackUser ();
    user.setId ( "ABC123" );
    user.setName ( "bot" );
    users.addUser ( user );
    assertEquals ( "@bot: hi all", processor.convertText ( "<@ABC123>: hi all", new SlackMessageDTO ( transport ) ) );
    assertEquals ( "<ABC123>: hi all", processor.convertText ( "<ABC123>: hi all", new SlackMessageDTO ( transport ) ) );
  }

}
