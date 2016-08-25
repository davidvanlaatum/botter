package au.id.vanlaatum.botter.transport.slack.Modal.RTM;

import au.id.vanlaatum.botter.transport.slack.Modal.SlackTimeStamp;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Marked extends BaseEvent {
  private String channel;
  private SlackTimeStamp ts;
  private SlackTimeStamp eventTS;
  private Integer unreadCount;
  private Integer unreadCountDisplay;
  private Integer numMentions;
  private Integer numMentionsDisplay;
  private Integer mentionCount;
  private Integer mentionCountDisplay;

  public String getChannel () {
    return channel;
  }

  public Marked setChannel ( String channel ) {
    this.channel = channel;
    return this;
  }

  public SlackTimeStamp getTs () {
    return ts;
  }

  public Marked setTs ( SlackTimeStamp ts ) {
    this.ts = ts;
    return this;
  }

  @JsonProperty ( "event_ts" )
  public SlackTimeStamp getEventTS () {
    return eventTS;
  }

  public Marked setEventTS ( SlackTimeStamp eventTS ) {
    this.eventTS = eventTS;
    return this;
  }

  @JsonProperty ( "unread_count" )
  public Integer getUnreadCount () {
    return unreadCount;
  }

  public Marked setUnreadCount ( Integer unreadCount ) {
    this.unreadCount = unreadCount;
    return this;
  }

  @JsonProperty ( "unread_count_display" )
  public Integer getUnreadCountDisplay () {
    return unreadCountDisplay;
  }

  public Marked setUnreadCountDisplay ( Integer unreadCountDisplay ) {
    this.unreadCountDisplay = unreadCountDisplay;
    return this;
  }

  @JsonProperty ( "num_mentions" )
  public Integer getNumMentions () {
    return numMentions;
  }

  public Marked setNumMentions ( Integer numMentions ) {
    this.numMentions = numMentions;
    return this;
  }

  @JsonProperty ( "num_mentions_display" )
  public Integer getNumMentionsDisplay () {
    return numMentionsDisplay;
  }

  public Marked setNumMentionsDisplay ( Integer numMentionsDisplay ) {
    this.numMentionsDisplay = numMentionsDisplay;
    return this;
  }

  @JsonProperty ( "mention_count" )
  public Integer getMentionCount () {
    return mentionCount;
  }

  public Marked setMentionCount ( Integer mentionCount ) {
    this.mentionCount = mentionCount;
    return this;
  }

  @JsonProperty ( "mention_count_display" )
  public Integer getMentionCountDisplay () {
    return mentionCountDisplay;
  }

  public Marked setMentionCountDisplay ( Integer mentionCountDisplay ) {
    this.mentionCountDisplay = mentionCountDisplay;
    return this;
  }
}
