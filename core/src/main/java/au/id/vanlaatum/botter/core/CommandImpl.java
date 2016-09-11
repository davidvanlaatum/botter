package au.id.vanlaatum.botter.core;

import au.id.vanlaatum.botter.api.Command;
import au.id.vanlaatum.botter.api.Message;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.api.User;
import org.apache.commons.lang3.text.StrMatcher;
import org.apache.commons.lang3.text.StrTokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class CommandImpl implements Command {
  private final List<String> commandParts = new ArrayList<> ();
  private final SortedMap<String, Object> attachments = new TreeMap<> ();
  private Message message;
  private String commandText;
  private User user;
  private Transport transport;

  @Override
  public Message getMessage () {
    return message;
  }

  public void setMessage ( Message message ) {
    this.message = message;
  }

  @Override
  public String getCommandText () {
    return commandText;
  }

  void setCommandText ( String commandText ) {
    this.commandText = commandText;
    splitParts ();
  }

  @Override
  public List<String> getCommandParts () {
    return Collections.unmodifiableList ( commandParts );
  }

  private void splitParts () {
    StrTokenizer tokenizer = new StrTokenizer ( commandText );
    tokenizer.setQuoteMatcher ( StrMatcher.quoteMatcher () );
    commandParts.addAll ( tokenizer.getTokenList () );
  }

  @Override
  public User getUser () {
    return user;
  }

  public void setUser ( User user ) {
    this.user = user;
  }

  @Override
  public Transport getTransport () {
    return transport;
  }

  public void setTransport ( Transport transport ) {
    this.transport = transport;
  }

  @Override
  public void reply ( String message ) {
    this.transport.reply ( this.message, message );
  }

  @Override
  public void error ( String message ) {
    this.transport.error ( this.message, message );
  }

  @Override
  public void annotate ( String message ) {
    this.transport.annotate ( this.message, message );
  }

  void removeCommandPart ( int i ) {
    commandParts.remove ( i );
  }

  @Override
  public void attach ( String name, Object value ) {
    attachments.put ( name, value );
  }

  @SuppressWarnings ( "unchecked" )
  @Override
  public <T> T getAttachment ( String name ) {
    return (T) attachments.get ( name );
  }
}
