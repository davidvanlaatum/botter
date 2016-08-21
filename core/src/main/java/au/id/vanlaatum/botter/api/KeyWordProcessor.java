package au.id.vanlaatum.botter.api;

import java.util.List;

public interface KeyWordProcessor {
  boolean checkForKeywords ( Command message, List<Boolean> used );
}
