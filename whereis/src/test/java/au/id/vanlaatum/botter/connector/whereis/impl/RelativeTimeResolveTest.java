package au.id.vanlaatum.botter.connector.whereis.impl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

@RunWith ( Parameterized.class )
public class RelativeTimeResolveTest {

  @Parameterized.Parameter ( 0 )
  public String description;
  @Parameterized.Parameter ( 1 )
  public DateTime expected;
  @Parameterized.Parameter ( 2 )
  public RelativeTime actual;

  @Parameterized.Parameters ( name = "{0}" )
  public static Collection<Object[]> data () {
    List<Object[]> rt = new ArrayList<> ();
    rt.add ( new Object[]{ "Fixed Date", date ( "2016-01-01 12:00:00GMT" ), new RelativeTime ( date ( "2016-01-01 12:00:00GMT" ) ) } );
    rt.add ( new Object[]{ "Now", date ( "2016-01-01 10:00:00GMT" ), new RelativeTime ( TimeConstant.NOW ) } );
    rt.add ( new Object[]{ "Sunday", date ( "2016-01-03 00:00:00GMT" ), new RelativeTime ( DateTimeConstants.SUNDAY ) } );
    rt.add ( new Object[]{ "Tuesday", date ( "2016-01-05 00:00:00GMT" ), new RelativeTime ( DateTimeConstants.TUESDAY ) } );
    rt.add ( new Object[]{ "10:15", date ( "2016-01-01 10:15:00GMT" ), new RelativeTime ( 10, 15 ) } );
    rt.add ( new Object[]{ "1:15", date ( "2016-01-01 13:15:00GMT" ), new RelativeTime ( 1, 15 ) } );
    rt.add ( new Object[]{ "Today", date ( "2016-01-01 00:00:00GMT" ), new RelativeTime ( TimeConstant.TODAY ) } );
    rt.add ( new Object[]{ "Tomorrow", date ( "2016-01-02 00:00:00GMT" ), new RelativeTime ( TimeConstant.TOMORROW ) } );
    return rt;
  }

  private static DateTime date ( String date ) {
    return DateTime.parse ( date, DateTimeFormat.forPattern ( "yyyy-MM-dd HH:mm:ssz" ) );
  }

  @Before
  public void setUp () throws Exception {
    DateTimeUtils.setCurrentMillisFixed ( date ( "2016-01-01 10:00:00GMT" ).getMillis () );
  }

  @After
  public void tearDown () throws Exception {
    DateTimeUtils.setCurrentMillisSystem ();
  }

  @Test
  public void resolveTime () throws Exception {
    assertEquals ( expected, actual.resolveTime ( TimeZone.getTimeZone ( "GMT" ) ) );
  }

}
