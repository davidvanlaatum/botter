package au.id.vanlaatum.botter.connector.mock.transport.api;

import org.hamcrest.Matcher;

public interface MessageAssert {
  void assertMessage ( MessageResponseType type, Matcher<String> message );

  void assertMessage ( MessageResponseType type, String message );

  enum MessageResponseType {
    REPLY,
    ERROR,
    ANNOTATE
  }


}
