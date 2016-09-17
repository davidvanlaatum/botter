package au.id.vanlaatum.botter.api;

import java.util.List;
import java.util.Map;

public interface BotFactory {
  void cleanCaches ();

  void processMessage ( Message message );

  boolean matchesPhrase ( List<Word> phrase, List<String> words, Map<String, Object> data );

  boolean matchesPhrase ( List<Word> phrase, List<String> words );
}
