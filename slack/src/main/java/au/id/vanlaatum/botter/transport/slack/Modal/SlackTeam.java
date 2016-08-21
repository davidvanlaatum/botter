package au.id.vanlaatum.botter.transport.slack.Modal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class SlackTeam {
  private String id;
  private String name;
  private String emailDomain;
  private String domain;
  private Integer msgEditWindowMins;
  private Map<String, Object> prefs;
  private Map<String, Object> icon;
  private Boolean overStorageLimit;
  private String plan;
  private Boolean overIntegrationsLimit;

  @JsonProperty("email_domain")
  public String getEmailDomain () {
    return emailDomain;
  }

  public void setEmailDomain ( String emailDomain ) {
    this.emailDomain = emailDomain;
  }

  public String getDomain () {
    return domain;
  }

  public void setDomain ( String domain ) {
    this.domain = domain;
  }

  @JsonProperty("msg_edit_window_mins")
  public Integer getMsgEditWindowMins () {
    return msgEditWindowMins;
  }

  public void setMsgEditWindowMins ( Integer msgEditWindowMins ) {
    this.msgEditWindowMins = msgEditWindowMins;
  }

  public Map<String, Object> getPrefs () {
    return prefs;
  }

  public void setPrefs ( Map<String, Object> prefs ) {
    this.prefs = prefs;
  }

  public Map<String, Object> getIcon () {
    return icon;
  }

  public void setIcon ( Map<String, Object> icon ) {
    this.icon = icon;
  }

  @JsonProperty("over_storage_limit")
  public Boolean getOverStorageLimit () {
    return overStorageLimit;
  }

  public void setOverStorageLimit ( Boolean overStorageLimit ) {
    this.overStorageLimit = overStorageLimit;
  }

  public String getPlan () {
    return plan;
  }

  public void setPlan ( String plan ) {
    this.plan = plan;
  }

  @JsonProperty("over_integrations_limit")
  public Boolean getOverIntegrationsLimit () {
    return overIntegrationsLimit;
  }

  public void setOverIntegrationsLimit ( Boolean overIntegrationsLimit ) {
    this.overIntegrationsLimit = overIntegrationsLimit;
  }

  public String getId () {
    return id;
  }

  public void setId ( String id ) {
    this.id = id;
  }

  public String getName () {
    return name;
  }

  public void setName ( String name ) {
    this.name = name;
  }
}
