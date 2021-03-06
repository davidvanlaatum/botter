package au.id.vanlaatum.botter.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface BotFactory {
  void cleanCaches ();

  Future processMessage ( Message message );

  boolean matchesPhrase ( List<Word> phrase, List<String> words, Map<String, Object> data );

  boolean matchesPhrase ( List<Word> phrase, List<String> words );

  void awaitBackgroundTasks () throws InterruptedException;
}
