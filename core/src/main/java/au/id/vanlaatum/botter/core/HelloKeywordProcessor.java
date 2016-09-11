package au.id.vanlaatum.botter.core;

import au.id.vanlaatum.botter.api.BotFactory;
import au.id.vanlaatum.botter.api.Command;
import au.id.vanlaatum.botter.api.KeyWordProcessor;
import au.id.vanlaatum.botter.api.StringWord;
import au.id.vanlaatum.botter.api.Word;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@OsgiServiceProvider ( classes = KeyWordProcessor.class )
@Named ( "HelloKeyword" )
public class HelloKeywordProcessor implements KeyWordProcessor {

  private static final String[][] GREETINGS = new String[][]{
      { "Hello" },
      { "hi" },
      { "good", "morning" },
      { "goodmorning" },
      { "morning" },
      { "evening" },
      { "good", "afternoon" },
      { "afternoon" },
      { "good", "evening" },
      { "evening" }
  };
  private static final String[] GREETINGS_RESPONSES = new String[]{
      "Hello",
      "hi"
  };
  private static final String[][] FAREWELLS = new String[][]{
      { "bye" },
      { "goodnight" },
      { "good", "night" },
      { "night" }
  };
  private static final String[] FAREWELL_RESPONSES = new String[]{
      "bye",
      "goodbye"
  };
  private Random random = new Random ();

  @Inject
  @Named ( "BotFactory" )
  private BotFactory botFactory;

  public HelloKeywordProcessor setBotFactory ( BotFactory botFactory ) {
    this.botFactory = botFactory;
    return this;
  }

  public HelloKeywordProcessor setRandom ( Random random ) {
    this.random = random;
    return this;
  }

  @Override
  public boolean checkForKeywords ( final Command message, List<Boolean> used ) {
    boolean rt = false;
    boolean hello = false;
    final Word addressesMe = new Word () {

      @Override
      public int matches ( List<String> text, Map<String, Object> data ) {
        if ( text.isEmpty () ) {
          return 0;
        } else if ( isAddressingMe ( message, text.get ( 0 ) ) ) {
          return 1;
        } else {
          return -1;
        }
      }
    };

    for ( String[] greeting : GREETINGS ) {
      List<Word> phrase = new ArrayList<> ();
      for ( String s : greeting ) {
        phrase.add ( new StringWord ( s ) );
      }
      phrase.add ( addressesMe );
      if ( botFactory.matchesPhrase ( phrase, message.getCommandParts () ) ) {
        rt = hello = true;
        break;
      }
    }

    if ( !rt ) {
      for ( String[] greeting : FAREWELLS ) {
        List<Word> phrase = new ArrayList<> ();
        for ( String s : greeting ) {
          phrase.add ( new StringWord ( s ) );
        }
        phrase.add ( addressesMe );
        if ( botFactory.matchesPhrase ( phrase, message.getCommandParts () ) ) {
          rt = true;
          break;
        }
      }
    }

    if ( rt ) {
      if ( hello ) {
        if ( message.getMessage ().getChannel ().isDirect () ) {
          message.reply ( randomGreeting () );
        } else {
          message.reply ( randomGreeting () + " " + message.getUser ().getName () );
        }
      } else {
        if ( message.getMessage ().getChannel ().isDirect () ) {
          message.reply ( randomFarewell () );
        } else {
          message.reply ( randomFarewell () + " " + message.getUser ().getName () );
        }
      }
    }

    return rt;
  }

  private String randomFarewell () {
    return FAREWELL_RESPONSES[random.nextInt ( FAREWELL_RESPONSES.length )];
  }

  private boolean isAddressingMe ( Command message, String text ) {
    return message.getTransport ().isMyName ( text ) || "all".equalsIgnoreCase ( text ) ||
        "everyone".equalsIgnoreCase ( text );
  }

  private String randomGreeting () {
    return GREETINGS_RESPONSES[random.nextInt ( GREETINGS_RESPONSES.length )];
  }
}
