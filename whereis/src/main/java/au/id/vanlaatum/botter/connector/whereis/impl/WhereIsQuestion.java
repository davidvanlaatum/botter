package au.id.vanlaatum.botter.connector.whereis.impl;

public class WhereIsQuestion implements Question {

  private final String userName;

  WhereIsQuestion ( String userName ) {
    this.userName = userName;
  }

  public String getUserName () {
    return userName;
  }

  @Override
  public QuestionType getType () {
    return QuestionType.WHEREIS;
  }
}
