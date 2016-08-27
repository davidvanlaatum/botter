package au.id.vanlaatum.botter.transport.slack;

public class Attachment {
  private String text;

  public Attachment ( String text ) {
    this.text = text;
  }

  public String getText () {
    return text;
  }

  public Attachment setText ( String text ) {
    this.text = text;
    return this;
  }
}
