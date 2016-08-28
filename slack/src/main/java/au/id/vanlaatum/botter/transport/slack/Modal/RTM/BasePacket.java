package au.id.vanlaatum.botter.transport.slack.Modal.RTM;

import au.id.vanlaatum.botter.transport.slack.Modal.SlackTimeStamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude ( JsonInclude.Include.NON_NULL )
public class BasePacket {
  protected Integer replyTo;
  private String raw;
  private Integer id;
  private JsonNode tree;
  private SlackTimeStamp ts;

  @JsonIgnore
  public String getRaw () {
    return raw;
  }

  public void setRaw ( String raw ) {
    this.raw = raw;
  }

  public Integer getId () {
    return id;
  }

  public void setId ( Integer id ) {
    this.id = id;
  }

  @JsonIgnore
  public JsonNode getTree () {
    return tree;
  }

  public void setTree ( JsonNode tree ) {
    this.tree = tree;
  }

  @JsonProperty ( "reply_to" )
  public Integer getReplyTo () {
    return replyTo;
  }

  public void setReplyTo ( Integer replyTo ) {
    this.replyTo = replyTo;
  }

  public SlackTimeStamp getTs () {
    return ts;
  }

  public void setTs ( SlackTimeStamp ts ) {
    this.ts = ts;
  }
}
