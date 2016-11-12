package au.id.vanlaatum.botter.transport.slack.modal.rtm;

import au.id.vanlaatum.botter.transport.slack.modal.SlackUserProfile;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageChannelJoin extends Message {
  private String inviter;
  private SlackUserProfile userProfile;

  public String getInviter () {
    return inviter;
  }

  public void setInviter ( String inviter ) {
    this.inviter = inviter;
  }

  @JsonProperty ( "user_profile" )
  public SlackUserProfile getUserProfile () {
    return userProfile;
  }

  public void setUserProfile ( SlackUserProfile userProfile ) {
    this.userProfile = userProfile;
  }
}
