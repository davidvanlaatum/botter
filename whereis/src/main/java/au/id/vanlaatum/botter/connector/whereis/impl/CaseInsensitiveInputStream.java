package au.id.vanlaatum.botter.connector.whereis.impl;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.IntStream;

public class CaseInsensitiveInputStream extends ANTLRInputStream {

  protected char[] lookaheadData;

  public CaseInsensitiveInputStream ( String input ) {
    super ( input );
    lookaheadData = input.toLowerCase ().toCharArray ();
  }

  @Override
  public int LA ( int i ) {
    if ( i == 0 ) {
      return 0;
    }
    if ( i < 0 ) {
      i++;
      if ( ( p + i - 1 ) < 0 ) {
        return IntStream.EOF;
      }
    }

    if ( ( p + i - 1 ) >= n ) {
      return IntStream.EOF;
    }

    return lookaheadData[p + i - 1];
  }

}
