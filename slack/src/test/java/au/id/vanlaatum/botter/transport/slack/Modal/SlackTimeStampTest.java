package au.id.vanlaatum.botter.transport.slack.Modal;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SlackTimeStampTest {
  @Test
  public void before () throws Exception {
    final SlackTimeStamp stamp1 = new SlackTimeStamp ( "1.1" );
    final SlackTimeStamp stamp2 = new SlackTimeStamp ( "1.2" );
    final SlackTimeStamp stamp3 = new SlackTimeStamp ( "2.0" );
    assertTrue ( stamp1.before ( stamp2 ) );
    assertFalse ( stamp2.before ( stamp1 ) );
    assertFalse ( stamp1.before ( stamp1 ) );
    assertTrue ( stamp1.before ( stamp3 ) );
    assertFalse ( stamp3.before ( stamp1 ) );
  }

}
