package au.id.vanlaatum.botter.core;

import au.id.vanlaatum.botter.api.Command;
import au.id.vanlaatum.botter.api.CommandProcessor;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static java.text.MessageFormat.format;

@OsgiServiceProvider ( classes = CommandProcessor.class )
@Named ( "HelpCommand" )
public class HelpCommand implements CommandProcessor {
  @Inject
  @Named ( "Commands" )
  private List<CommandProcessor> commands;

  @Override
  public String getName () {
    return "help";
  }

  @Override
  public String getHelp () {
    return "help: returns useful help";
  }

  @Override
  public boolean process ( Command command ) {
    boolean rt = false;
    if ( "help".equalsIgnoreCase ( command.getCommandParts ().get ( 0 ) ) ) {
      StringBuilder buffer = new StringBuilder ();
      for ( CommandProcessor processor : commands ) {
        buffer.append ( format ( "{0}\n", processor.getHelp () ) );
      }
      command.reply ( buffer.toString ().trim () );
      rt = true;
    }
    return rt;
  }
}
