package au.id.vanlaatum.botter.connector.weather.impl;

public class SetLocation implements Question {
  private final String city;
  private final String country;

  public SetLocation ( String city, String country ) {
    this.city = city;
    this.country = country;
  }

  @Override
  public QuestionType getType () {
    return QuestionType.SETLOCATION;
  }

  public String getCity () {
    return city;
  }

  public String getCountry () {
    return country;
  }
}
