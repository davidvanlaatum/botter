package au.id.vanlaatum.botter.transport.slack.Modal.RTM;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

public class Attachment {
  private String text;
  private String serviceName;
  private String title;
  private URI titleLink;
  private String fallback;
  private URI thumbUrl;
  private URI fromUrl;
  private Integer thumbWidth;
  private Integer thumbHeight;
  private URI serviceIcon;
  private Integer id;

  public Attachment ( String text ) {
    this.text = text;
  }

  public Attachment () {
  }

  public String getText () {
    return text;
  }

  public Attachment setText ( String text ) {
    this.text = text;
    return this;
  }

  @JsonProperty ( "service_name" )
  public String getServiceName () {
    return serviceName;
  }

  public Attachment setServiceName ( String serviceName ) {
    this.serviceName = serviceName;
    return this;
  }

  public String getTitle () {
    return title;
  }

  public Attachment setTitle ( String title ) {
    this.title = title;
    return this;
  }

  @JsonProperty ( "title_link" )
  public URI getTitleLink () {
    return titleLink;
  }

  public Attachment setTitleLink ( URI titleLink ) {
    this.titleLink = titleLink;
    return this;
  }

  public String getFallback () {
    return fallback;
  }

  public Attachment setFallback ( String fallback ) {
    this.fallback = fallback;
    return this;
  }

  @JsonProperty ( "thumb_url" )
  public URI getThumbUrl () {
    return thumbUrl;
  }

  public Attachment setThumbUrl ( URI thumbUrl ) {
    this.thumbUrl = thumbUrl;
    return this;
  }

  @JsonProperty ( "from_url" )
  public URI getFromUrl () {
    return fromUrl;
  }

  public Attachment setFromUrl ( URI fromUrl ) {
    this.fromUrl = fromUrl;
    return this;
  }

  @JsonProperty ( "thumb_width" )
  public Integer getThumbWidth () {
    return thumbWidth;
  }

  public Attachment setThumbWidth ( Integer thumbWidth ) {
    this.thumbWidth = thumbWidth;
    return this;
  }

  @JsonProperty ( "thumb_height" )
  public Integer getThumbHeight () {
    return thumbHeight;
  }

  public Attachment setThumbHeight ( Integer thumbHeight ) {
    this.thumbHeight = thumbHeight;
    return this;
  }

  @JsonProperty ( "service_icon" )
  public URI getServiceIcon () {
    return serviceIcon;
  }

  public Attachment setServiceIcon ( URI serviceIcon ) {
    this.serviceIcon = serviceIcon;
    return this;
  }

  public Integer getId () {
    return id;
  }

  public Attachment setId ( Integer id ) {
    this.id = id;
    return this;
  }
}
