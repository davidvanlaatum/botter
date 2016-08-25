package au.id.vanlaatum.botter.transport.slack.Modal.RTM;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IMMarked extends Marked {

  private Integer dmCount;

  @JsonProperty("dm_count")
  public Integer getDMCount () {
    return dmCount;
  }

  public IMMarked setDMCount ( Integer dmCount ) {
    this.dmCount = dmCount;
    return this;
  }
}
