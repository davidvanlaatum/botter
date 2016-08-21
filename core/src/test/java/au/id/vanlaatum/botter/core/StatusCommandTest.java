package au.id.vanlaatum.botter.core;

import au.id.vanlaatum.botter.api.Command;
import au.id.vanlaatum.botter.api.StatusInfoProvider;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class StatusCommandTest {
  @Test
  public void process () throws Exception {
    final StatusInfoProvider provider = mock ( StatusInfoProvider.class );
    when ( provider.getStatus () ).thenReturn ( "Hi all" );
    StatusCommand status = new StatusCommand ();
    status.setStatusProviders ( Collections.singletonList ( provider ) );

    Command command = mock ( Command.class );
    when ( command.getCommandParts () ).thenReturn ( Collections.singletonList ( "status" ) );

    status.process ( command );

    verify ( command ).reply ( "Hi all" );
  }

}
