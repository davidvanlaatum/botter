package au.id.vanlaatum.botter.api;

import java.util.List;

public interface BotFactory {
  void processMessage ( Message message );

  boolean matchesPhrase ( List<Word> phrase, List<String> words );
}
