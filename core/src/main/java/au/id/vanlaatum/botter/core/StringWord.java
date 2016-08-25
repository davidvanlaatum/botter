package au.id.vanlaatum.botter.core;

import au.id.vanlaatum.botter.api.Word;

import java.util.List;

public class StringWord implements Word {

  private String word;

  public StringWord ( String word ) {
    this.word = word;
  }

  @Override
  public int matches ( List<String> text ) {
    return !text.isEmpty () && word.equalsIgnoreCase ( text.get ( 0 ) ) ? 1 : -1;
  }
}
