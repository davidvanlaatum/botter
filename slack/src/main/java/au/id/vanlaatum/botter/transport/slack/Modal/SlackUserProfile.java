package au.id.vanlaatum.botter.transport.slack.Modal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;


public class SlackUserProfile {
  private String avatarHash;
  private String firstName;
  private String lastName;
  private String name;
  private String realName;
  private String realNameNormalized;
  private String email;
  private String title;
  private URI image24;
  private URI image32;
  private URI image48;
  private URI image72;
  private URI image192;
  private URI image512;
  private URI image1024;
  private URI imageOriginal;
  private Object fields;
  private String botID;
  private String APIAppID;
  private String phone;
  private String skype;

  public String getSkype () {
    return skype;
  }

  public void setSkype ( String skype ) {
    this.skype = skype;
  }

  public String getPhone () {
    return phone;
  }

  public void setPhone ( String phone ) {
    this.phone = phone;
  }

  public String getTitle () {
    return title;
  }

  public void setTitle ( String title ) {
    this.title = title;
  }

  @JsonProperty ( "image_original" )
  public URI getImageOriginal () {
    return imageOriginal;
  }

  public void setImageOriginal ( URI imageOriginal ) {
    this.imageOriginal = imageOriginal;
  }

  @JsonProperty ( "bot_id" )
  public String getBotID () {
    return botID;
  }

  public void setBotID ( String botID ) {
    this.botID = botID;
  }

  @JsonProperty ( "api_app_id" )
  public String getAPIAppID () {
    return APIAppID;
  }

  public void setAPIAppID ( String APIAppID ) {
    this.APIAppID = APIAppID;
  }

  public Object getFields () {
    return fields;
  }

  public void setFields ( Object fields ) {
    this.fields = fields;
  }

  public String getEmail () {
    return email;
  }

  public void setEmail ( String email ) {
    this.email = email;
  }

  @JsonProperty ( "image_24" )
  public URI getImage24 () {
    return image24;
  }

  public void setImage24 ( URI image24 ) {
    this.image24 = image24;
  }

  @JsonProperty ( "image_32" )
  public URI getImage32 () {
    return image32;
  }

  public void setImage32 ( URI image32 ) {
    this.image32 = image32;
  }

  @JsonProperty ( "image_48" )
  public URI getImage48 () {
    return image48;
  }

  public void setImage48 ( URI image48 ) {
    this.image48 = image48;
  }

  @JsonProperty ( "image_192" )
  public URI getImage192 () {
    return image192;
  }

  public void setImage192 ( URI image192 ) {
    this.image192 = image192;
  }

  @JsonProperty ( "image_512" )
  public URI getImage512 () {
    return image512;
  }

  public void setImage512 ( URI image512 ) {
    this.image512 = image512;
  }

  @JsonProperty ( "image_1024" )
  public URI getImage1024 () {
    return image1024;
  }

  public void setImage1024 ( URI image1024 ) {
    this.image1024 = image1024;
  }

  @JsonProperty ( "image_72" )
  public URI getImage72 () {
    return image72;
  }

  public void setImage72 ( URI image72 ) {
    this.image72 = image72;
  }

  @JsonProperty ( "avatar_hash" )
  public String getAvatarHash () {
    return avatarHash;
  }

  public void setAvatarHash ( String avatarHash ) {
    this.avatarHash = avatarHash;

  }

  @JsonProperty ( "first_name" )
  public String getFirstName () {
    return firstName;
  }

  public void setFirstName ( String firstName ) {
    this.firstName = firstName;
  }

  @JsonProperty ( "last_name" )
  public String getLastName () {
    return lastName;
  }

  public void setLastName ( String lastName ) {
    this.lastName = lastName;
  }

  public String getName () {
    return name;
  }

  public void setName ( String name ) {
    this.name = name;
  }

  @JsonProperty ( "real_name" )
  public String getRealName () {
    return realName;
  }

  public void setRealName ( String realName ) {
    this.realName = realName;
  }

  @JsonProperty ( "real_name_normalized" )
  public String getRealNameNormalized () {
    return realNameNormalized;
  }

  public void setRealNameNormalized ( String realNameNormalized ) {
    this.realNameNormalized = realNameNormalized;
  }
}
