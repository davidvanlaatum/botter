package au.id.vanlaatum.botter.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StringWord implements Word {

  private String word;

  public StringWord ( String word ) {
    this.word = word;
  }

  public static List<Word> toWords ( String... words ) {
    List<Word> rt = new ArrayList<> ();

    for ( String word : words ) {
      rt.add ( new StringWord ( word ) );
    }

    return rt;
  }

  @Override
  public int matches ( List<String> text, Map<String, Object> data ) {
    return !text.isEmpty () && word.equalsIgnoreCase ( text.get ( 0 ) ) ? 1 : -1;
  }
}
