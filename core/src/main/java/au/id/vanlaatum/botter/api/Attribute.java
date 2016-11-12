package au.id.vanlaatum.botter.api;

import org.osgi.service.metatype.AttributeDefinition;

public class Attribute implements AttributeDefinition {

  private String name;
  private String id;
  private String description;
  private String[] defaultValue = new String[]{ "" };
  private int type;

  @Override
  public String getName () {
    return name;
  }

  public Attribute setName ( String name ) {
    this.name = name;
    return this;
  }

  public Attribute setId ( String id ) {
    this.id = id;
    return this;
  }

  @Override
  public String getID () {
    return id;
  }

  @Override
  public String getDescription () {
    return description;
  }

  public Attribute setDescription ( String description ) {
    this.description = description;
    return this;
  }

  @Override
  public int getCardinality () {
    return 0;
  }

  @Override
  public int getType () {
    return type;
  }

  public Attribute setType ( int type ) {
    this.type = type;
    return this;
  }

  @Override
  public String[] getOptionValues () {
    return new String[0];
  }

  @Override
  public String[] getOptionLabels () {
    return new String[0];
  }

  @Override
  public String validate ( String s ) {
    return null;
  }

  @Override
  public String[] getDefaultValue () {
    return defaultValue.clone ();
  }

  public Attribute setDefaultValue ( String defaultValue ) {
    this.defaultValue = new String[]{ defaultValue };
    return this;
  }
}
