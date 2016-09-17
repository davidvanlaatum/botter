package au.id.vanlaatum.botter.core;

import au.id.vanlaatum.botter.api.BotFactory;
import au.id.vanlaatum.botter.api.CommandProcessor;
import au.id.vanlaatum.botter.api.GenericCache;
import au.id.vanlaatum.botter.api.KeyWordProcessor;
import au.id.vanlaatum.botter.api.Message;
import au.id.vanlaatum.botter.api.StatusInfoProvider;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.api.Word;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static java.text.MessageFormat.format;

@Named ( "BotFactory" )
@Singleton
public class BotFactoryImpl implements BotFactory, MetaTypeProvider {

  @Inject
  @Named ( "blueprintBundleContext" )
  private BundleContext context;
  @Inject
  @Named ( "logService" )
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
  @Inject
  @Named ( "Caches" )
  private List<GenericCache<?, ?>> cacheList;
  private ScheduledExecutorService executor;
  private ThreadGroup threadGroup;
  private ServiceRegistration<StatusInfoProvider> statusProvider;
  private int threadId = 1;

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

  public BotFactoryImpl setCacheList ( List<GenericCache<?, ?>> cacheList ) {
    this.cacheList = cacheList;
    return this;
  }

  @PostConstruct
  public void init () {
    threadGroup = new ThreadGroup ( "Botter Command Processing" );
    executor = Executors.newScheduledThreadPool ( 1, new ThreadFactory () {

      @Override
      public Thread newThread ( Runnable r ) {
        Thread thread = new Thread ( threadGroup, r, format ( "Bot CMD {0}", threadId++ ) );
        thread.setDaemon ( true );
        return thread;
      }
    } );
    executor.scheduleAtFixedRate ( new Runnable () {
      @Override
      public void run () {
        cleanCaches ();
      }
    }, 0, 10, TimeUnit.SECONDS );
    statusProvider =
        context.registerService ( StatusInfoProvider.class, new StatusProvider (), new Hashtable<String, Object> () );
  }

  @Override
  public void cleanCaches () {
    for ( GenericCache<?, ?> cache : cacheList ) {
      cache.cleanup ();
    }
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
  public void processMessage ( final Message message ) {
    log.log ( LogService.LOG_INFO, format ( "Received message {0} on {1} from {2}", message.getText (),
        message.getChannel ().getName (), message.getUser ().getName () ) );
    final CommandImpl command = new CommandImpl ();
    command.setMessage ( message );
    command.setTransport ( message.getTransport () );
    command.setUser ( message.getUser () );
    command.setCommandText ( message.getText () );

    executor.submit ( new Runnable () {
      @Override
      public void run () {
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
    } );
  }

  @Override
  public boolean matchesPhrase ( List<Word> phrase, List<String> words ) {
    return matchesPhrase ( phrase, words, new HashMap<String, Object> () );
  }

  @Override
  public boolean matchesPhrase ( List<Word> phrase, List<String> words, Map<String, Object> data ) {
    boolean rt = true;
    final ArrayList<String> list = new ArrayList<> ( words );
    for ( Word word : phrase ) {
      int len = word.matches ( list, data );
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
      try {
        if ( processor.checkForKeywords ( command, used ) ) {
          found = true;
        }
      } catch ( Throwable ex ) {
        log.log ( LogService.LOG_WARNING, "Exception processing keyword processor " + processor, ex );
      }
    }
    return found;
  }

  protected void processCommand ( final CommandImpl command, final boolean tryKeyWords ) {
    if ( command.getMessage ().getUser ().isUser () ) {
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
      return format ( "{0} Commands\n{1} Transports\n{2} Keywords\n{3} Caches containing {4} items\n", commands.size (), transports.size (),
          keyWordProcessors.size (), cacheList.size (), sumCaches () );
    }

    private int sumCaches () {
      int rt = 0;
      for ( GenericCache<?, ?> cache : cacheList ) {
        rt += cache.size ();
      }
      return rt;
    }
  }
}
