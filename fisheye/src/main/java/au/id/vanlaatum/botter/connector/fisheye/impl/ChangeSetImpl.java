package au.id.vanlaatum.botter.connector.fisheye.impl;

import au.id.vanlaatum.botter.connector.fisheye.api.ChangeSet;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ChangeSetImpl implements ChangeSet {
  private String repositoryName;
  private String csid;
  private String displayId;
  private String position;
  private List<String> parents;
  private List<String> children;
  private List<String> branches;
  private List<String> tags;
  private List<String> p4JobIds;
  private Date date;
  private String author;
  private String comment;
  private String branch;
  private List<File> fileRevisionKey;

  public List<File> getFileRevisionKey () {
    return Collections.unmodifiableList ( fileRevisionKey );
  }

  @JsonDeserialize ( contentAs = FileImpl.class )
  public ChangeSetImpl setFileRevisionKey ( List<File> fileRevisionKey ) {
    this.fileRevisionKey = new ArrayList<> ( fileRevisionKey );
    return this;
  }

  public String getBranch () {
    return branch;
  }

  public ChangeSetImpl setBranch ( String branch ) {
    this.branch = branch;
    return this;
  }

  public List<String> getP4JobIds () {
    return Collections.unmodifiableList ( p4JobIds );
  }

  public ChangeSetImpl setP4JobIds ( List<String> p4JobIds ) {
    this.p4JobIds = new ArrayList<> ( p4JobIds );
    return this;
  }

  public List<String> getTags () {
    return Collections.unmodifiableList ( tags );
  }

  public ChangeSetImpl setTags ( List<String> tags ) {
    this.tags = new ArrayList<> ( tags );
    return this;
  }

  public List<String> getBranches () {
    return Collections.unmodifiableList ( branches );
  }

  public ChangeSetImpl setBranches ( List<String> branches ) {
    this.branches = new ArrayList<> ( branches );
    return this;
  }

  public List<String> getChildren () {
    return Collections.unmodifiableList ( children );
  }

  public ChangeSetImpl setChildren ( List<String> children ) {
    this.children = new ArrayList<> ( children );
    return this;
  }

  public List<String> getParents () {
    return Collections.unmodifiableList ( parents );
  }

  public ChangeSetImpl setParents ( List<String> parents ) {
    this.parents = new ArrayList<> ( parents );
    return this;
  }

  public String getPosition () {
    return position;
  }

  public ChangeSetImpl setPosition ( String position ) {
    this.position = position;
    return this;
  }

  public String getDisplayId () {
    return displayId;
  }

  public ChangeSetImpl setDisplayId ( String displayId ) {
    this.displayId = displayId;
    return this;
  }

  public String getCsid () {
    return csid;
  }

  public ChangeSetImpl setCsid ( String csid ) {
    this.csid = csid;
    return this;

  }

  @Override
  public String getID () {
    return csid;
  }

  @Override
  public String getRepositoryName () {
    return repositoryName;
  }

  public ChangeSetImpl setRepositoryName ( String repositoryName ) {
    this.repositoryName = repositoryName;
    return this;
  }

  @Override
  public String getAuthor () {
    return author;
  }

  public ChangeSetImpl setAuthor ( String author ) {
    this.author = author;
    return this;
  }

  @Override
  public String getComment () {
    return comment;
  }

  public ChangeSetImpl setComment ( String comment ) {
    this.comment = comment;
    return this;
  }

  @Override
  public Date getDate () {
    return ObjectUtils.clone ( date );
  }

  public ChangeSetImpl setDate ( Date date ) {
    this.date = ObjectUtils.clone ( date );
    return this;
  }

  @Override
  public List<File> getFiles () {
    return Collections.unmodifiableList ( fileRevisionKey );
  }

  private static class FileImpl implements File {
    private String path;
    private String rev;

    @Override
    public String getPath () {
      return path;
    }

    public FileImpl setPath ( String path ) {
      this.path = path;
      return this;
    }

    public String getRev () {
      return rev;
    }

    public FileImpl setRev ( String rev ) {
      this.rev = rev;
      return this;
    }
  }
}
