package au.id.vanlaatum.botter.connector.fisheye.impl;

import au.id.vanlaatum.botter.connector.fisheye.api.ChangeSet;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import java.net.URI;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class FisheyeConnectorImplTest implements LogService {
  @Test
  public void getChangeSet () throws Exception {
    FisheyeConnectorImpl connector = new FisheyeConnectorImpl ( "123" );
    CloseableHttpClient client = mock ( CloseableHttpClient.class );
    CloseableHttpResponse response = mock ( CloseableHttpResponse.class );
    when ( client.execute ( Mockito.any ( HttpUriRequest.class ) ) ).thenReturn ( response );
    final StatusLine statusLine = mock ( StatusLine.class );
    when ( statusLine.getStatusCode () ).thenReturn ( 200 );
    when ( response.getStatusLine () ).thenReturn ( statusLine );
    when ( response.getEntity () ).thenReturn ( new StringEntity ( "{\n" +
        "  \"repositoryName\": \"repositoryName\",\n" +
        "  \"csid\": \"changeset_id\",\n" +
        "  \"displayId\": \"changeset_display_id\",\n" +
        "  \"position\": \"0\",\n" +
        "  \"parents\": [\n" +
        "    \"parent\"\n" +
        "  ],\n" +
        "  \"children\": [\n" +
        "    \"child2\",\n" +
        "    \"child1\"\n" +
        "  ],\n" +
        "  \"date\": 1467980791078,\n" +
        "  \"author\": \"author\",\n" +
        "  \"branches\": [\n" +
        "    \"branch\"\n" +
        "  ],\n" +
        "  \"tags\": [\n" +
        "    \"tag1\",\n" +
        "    \"tag2\"\n" +
        "  ],\n" +
        "  \"comment\": \"comment\",\n" +
        "  \"p4JobIds\": [\n" +
        "    \"job1\",\n" +
        "    \"job2\",\n" +
        "    \"job3\"\n" +
        "  ],\n" +
        "  \"branch\": \"branch\",\n" +
        "  \"fileRevisionKey\": [\n" +
        "    {\n" +
        "      \"path\": \"dir/path/path/\",\n" +
        "      \"rev\": \"revision_id\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"path\": \"dir2/path2/path2/\",\n" +
        "      \"rev\": \"revision_id_2\"\n" +
        "    }\n" +
        "  ]\n" +
        "}" ) );
    connector.setClient ( client );
    connector.setLog ( this );
    connector.setURI ( new URI ( "http://fisheye/" ) );
    final ChangeSet changeSet = connector.getChangeSet ( "bla", "123" );
    assertEquals ( changeSet.getID (), "changeset_id" );
    assertEquals ( changeSet.getAuthor (), "author" );
    assertEquals ( changeSet.getComment (), "comment" );
    assertEquals ( changeSet.getRepositoryName (), "repositoryName" );
    assertEquals ( changeSet.getDate (), new Date ( 1467980791078L ) );
    assertThat ( changeSet.getFiles (), hasSize ( 2 ) );
    assertThat ( changeSet.getFiles ().get ( 0 ).getPath (), equalTo ( "dir/path/path/" ) );
    assertThat ( changeSet.getFiles ().get ( 1 ).getPath (), equalTo ( "dir2/path2/path2/" ) );
  }


  @Override
  public void log ( int level, String message ) {
    log ( level, message, null );
  }

  @Override
  public void log ( int level, String message, Throwable exception ) {
    System.out.println ( message );
    if ( exception != null ) {
      exception.printStackTrace ();
    }
  }

  @Override
  public void log ( ServiceReference sr, int level, String message ) {
    log ( level, message );
  }

  @Override
  public void log ( ServiceReference sr, int level, String message, Throwable exception ) {
    log ( level, message, exception );
  }
}
