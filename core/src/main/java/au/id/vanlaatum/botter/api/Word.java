package au.id.vanlaatum.botter.api;

import java.util.List;
import java.util.Map;

public interface Word {
  int matches ( List<String> text, Map<String, Object> data );
}
