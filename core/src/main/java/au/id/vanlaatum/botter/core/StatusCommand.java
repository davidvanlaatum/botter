package au.id.vanlaatum.botter.core;

import au.id.vanlaatum.botter.api.BotFactory;
import au.id.vanlaatum.botter.api.Command;
import au.id.vanlaatum.botter.api.CommandProcessor;
import au.id.vanlaatum.botter.api.StatusInfoProvider;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@OsgiServiceProvider ( classes = CommandProcessor.class )
@Named ( "StatusCommand" )
public class StatusCommand implements CommandProcessor {
  @Inject
  private BotFactory botFactory;
  @Inject
  @Named ( "StatusProviders" )
  private List<StatusInfoProvider> statusProviders;

  public void setStatusProviders ( List<StatusInfoProvider> statusProviders ) {
    this.statusProviders = new ArrayList<> ( statusProviders );
  }

  public void setBotFactory ( BotFactory botFactory ) {
    this.botFactory = botFactory;
  }

  @Override
  public String getName () {
    return "Status";
  }

  @Override
  public String getHelp () {
    return "status: show bot status";
  }

  @Override
  public boolean process ( Command command ) {
    boolean rt = false;
    if ( "status".equalsIgnoreCase ( command.getCommandParts ().get ( 0 ) ) && command.getCommandParts ().size () == 1 ) {
      StringBuilder buffer = new StringBuilder ();
      for ( StatusInfoProvider provider : statusProviders ) {
        buffer.append ( provider.getStatus ().trim () ).append ( "\n" );
      }
      command.reply ( buffer.toString ().trim () );
      rt = true;
    }
    return rt;
  }
}
