package au.id.vanlaatum.botter.core;

import au.id.vanlaatum.botter.api.BotFactory;
import au.id.vanlaatum.botter.api.CommandProcessor;
import au.id.vanlaatum.botter.api.KeyWordProcessor;
import au.id.vanlaatum.botter.api.Message;
import au.id.vanlaatum.botter.api.StatusInfoProvider;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.api.Word;
import org.ops4j.pax.cdi.api.OsgiService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static java.text.MessageFormat.format;

@Named ( "BotFactory" )
@Singleton
public class BotFactoryImpl implements BotFactory, ManagedService, MetaTypeProvider {

  @Inject
  @Named ( "blueprintBundleContext" )
  private BundleContext context;
  @Inject
  @OsgiService
  private LogService log;
  @Inject
  @Named ( "Transports" )
  private List<Transport> transports;
  @Inject
  @Named ( "Commands" )
  private List<CommandProcessor> commands;
  @Inject
  @Named ( "Keywords" )
  private List<KeyWordProcessor> keyWordProcessors;
  private ExecutorService executor;
  private ThreadGroup threadGroup;
  private ServiceRegistration<StatusInfoProvider> statusProvider;

  public BotFactoryImpl () {
  }

  public void setTransports ( List<Transport> transports ) {
    this.transports = transports;
  }

  public void setCommands ( List<CommandProcessor> commands ) {
    this.commands = commands;
  }

  public void setKeyWordProcessors ( List<KeyWordProcessor> keyWordProcessors ) {
    this.keyWordProcessors = keyWordProcessors;
  }

  public void updated ( Dictionary<String, ?> dictionary ) throws ConfigurationException {
    log.log ( LogService.LOG_INFO, format ( "Received config {0}", dictionary ) );
  }

  @PostConstruct
  public void init () {
    threadGroup = new ThreadGroup ( "Botter Command Processing" );
    executor = Executors.newCachedThreadPool ( new ThreadFactory () {
      private int threadId = 1;

      @Override
      public Thread newThread ( Runnable r ) {
        Thread thread = new Thread ( threadGroup, r, format ( "Bot CMD {0}", threadId++ ) );
        thread.setDaemon ( true );
        return thread;
      }
    } );
    statusProvider =
        context.registerService ( StatusInfoProvider.class, new StatusProvider (), new Hashtable<String, Object> () );
  }

  @PreDestroy
  public void shutdown () {
    threadGroup.destroy ();
    threadGroup = null;
    if ( statusProvider != null ) {
      statusProvider.unregister ();
    }
  }

  public ObjectClassDefinition getObjectClassDefinition ( String s, String s1 ) {
    log.log ( LogService.LOG_INFO, format ( "getObjectClassDefinition({0},{1})", s, s1 ) );
    return new ObjectClassDefinition () {
      public String getName () {
        return "Bot Factory";
      }

      public String getID () {
        return "BotFactory";
      }

      public String getDescription () {
        return "Bot Factory";
      }

      public AttributeDefinition[] getAttributeDefinitions ( int i ) {
        return new AttributeDefinition[]{};
      }

      public InputStream getIcon ( int i ) throws IOException {
        return null;
      }
    };
  }

  public String[] getLocales () {
    return new String[]{ "en" };
  }

  @Override
  public void processMessage ( Message message ) {
    log.log ( LogService.LOG_INFO, format ( "Received message {0} on {1} from {2}", message.getText (),
        message.getChannel ().getName (), message.getUser ().getName () ) );
    CommandImpl command = new CommandImpl ();
    command.setMessage ( message );
    command.setTransport ( message.getTransport () );
    command.setUser ( message.getUser () );
    command.setCommandText ( message.getText () );
    if ( message.getChannel ().isDirect () ) {
      processCommand ( command, true );
    } else if ( message.getTransport ().isMyName ( command.getCommandParts ().get ( 0 ) ) ) {
      command.removeCommandPart ( 0 );
      processCommand ( command, false );
    } else {
      if ( !processKeyWords ( command ) ) {
        log.log ( LogService.LOG_INFO, "No keywords" );
      }
    }
  }

  @Override
  public boolean matchesPhrase ( List<Word> phrase, List<String> words ) {
    boolean rt = true;
    final ArrayList<String> list = new ArrayList<> ( words );
    for ( Word word : phrase ) {
      int len = word.matches ( list );
      if ( len < 0 ) {
        rt = false;
        break;
      }
      while ( len > 0 ) {
        list.remove ( 0 );
        len--;
      }
    }
    return rt && list.isEmpty ();
  }

  private boolean processKeyWords ( CommandImpl command ) {
    boolean found = false;
    List<Boolean> used = new ArrayList<> ( command.getCommandParts ().size () );
    Collections.fill ( used, false );
    for ( KeyWordProcessor processor : keyWordProcessors ) {
      if ( processor.checkForKeywords ( command, used ) ) {
        found = true;
      }
    }
    return found;
  }

  protected void processCommand ( final CommandImpl command, final boolean tryKeyWords ) {
    if ( command.getMessage ().getUser ().isUser () ) {
      executor.submit ( new Runnable () {
        @Override
        public void run () {
          boolean found = false;
          for ( CommandProcessor processor : commands ) {
            if ( processor.process ( command ) ) {
              found = true;
              break;
            }
          }
          if ( !found && ( !tryKeyWords || !processKeyWords ( command ) ) ) {
            log.log ( LogService.LOG_WARNING, "No such command " + command.getCommandText () );
            command.error ( "No such command: " + command.getCommandText () + "\nuse help for list" );
          }
        }
      } );
    } else {
      log.log ( LogService.LOG_WARNING,
          "Ignoring command from user " + command.getMessage ().getUser ().getName () + " not a permitted user" );
    }
  }

  private class StatusProvider implements StatusInfoProvider {
    @Override
    public String getName () {
      return "factory";
    }

    @Override
    public String getStatus () {
      return format ( "{0} Commands\n", commands.size () ) +
          format ( "{0} Transports\n", transports.size () );
    }
  }
}
