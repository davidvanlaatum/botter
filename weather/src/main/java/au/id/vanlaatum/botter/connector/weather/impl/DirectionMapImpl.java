package au.id.vanlaatum.botter.connector.weather.impl;

import au.id.vanlaatum.botter.connector.weather.api.DirectionMap;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@OsgiServiceProvider ( classes = DirectionMap.class )
@Singleton
public class DirectionMapImpl implements DirectionMap {
  private static final List<Direction> directions = new ArrayList<> ();

  static {
    directions.add ( new Direction ( "N", new BigDecimal ( "0" ), new BigDecimal ( "5.62" ) ) );
    directions.add ( new Direction ( "NbE", new BigDecimal ( "5.63" ), new BigDecimal ( "16.87" ) ) );
    directions.add ( new Direction ( "NNE", new BigDecimal ( "16.88" ), new BigDecimal ( "28.12" ) ) );
    directions.add ( new Direction ( "NEbN", new BigDecimal ( "28.13" ), new BigDecimal ( "39.37" ) ) );
    directions.add ( new Direction ( "NE", new BigDecimal ( "39.38" ), new BigDecimal ( "50.62" ) ) );
    directions.add ( new Direction ( "NEbE", new BigDecimal ( "50.63" ), new BigDecimal ( "61.87" ) ) );
    directions.add ( new Direction ( "ENE", new BigDecimal ( "61.88" ), new BigDecimal ( "73.12" ) ) );
    directions.add ( new Direction ( "EbN", new BigDecimal ( "73.13" ), new BigDecimal ( "84.37" ) ) );
    directions.add ( new Direction ( "E", new BigDecimal ( "84.38" ), new BigDecimal ( "95.62" ) ) );
    directions.add ( new Direction ( "EbS", new BigDecimal ( "95.63" ), new BigDecimal ( "106.87" ) ) );
    directions.add ( new Direction ( "ESE", new BigDecimal ( "106.88" ), new BigDecimal ( "118.12" ) ) );
    directions.add ( new Direction ( "SEbE", new BigDecimal ( "118.13" ), new BigDecimal ( "129.37" ) ) );
    directions.add ( new Direction ( "SE", new BigDecimal ( "129.38" ), new BigDecimal ( "140.62" ) ) );
    directions.add ( new Direction ( "SEbS", new BigDecimal ( "140.63" ), new BigDecimal ( "151.87" ) ) );
    directions.add ( new Direction ( "SSE", new BigDecimal ( "151.88" ), new BigDecimal ( "163.12" ) ) );
    directions.add ( new Direction ( "SbE", new BigDecimal ( "163.13" ), new BigDecimal ( "174.37" ) ) );
    directions.add ( new Direction ( "S", new BigDecimal ( "174.38" ), new BigDecimal ( "185.62" ) ) );
    directions.add ( new Direction ( "SbW", new BigDecimal ( "185.63" ), new BigDecimal ( "196.87" ) ) );
    directions.add ( new Direction ( "SSW", new BigDecimal ( "196.88" ), new BigDecimal ( "208.12" ) ) );
    directions.add ( new Direction ( "SWbS", new BigDecimal ( "208.13" ), new BigDecimal ( "219.37" ) ) );
    directions.add ( new Direction ( "SW", new BigDecimal ( "219.38" ), new BigDecimal ( "230.62" ) ) );
    directions.add ( new Direction ( "SWbW", new BigDecimal ( "230.63" ), new BigDecimal ( "241.87" ) ) );
    directions.add ( new Direction ( "WSW", new BigDecimal ( "241.88" ), new BigDecimal ( "253.12" ) ) );
    directions.add ( new Direction ( "WbS", new BigDecimal ( "253.13" ), new BigDecimal ( "264.37" ) ) );
    directions.add ( new Direction ( "W", new BigDecimal ( "264.38" ), new BigDecimal ( "275.62" ) ) );
    directions.add ( new Direction ( "WbN", new BigDecimal ( "275.63" ), new BigDecimal ( "286.87" ) ) );
    directions.add ( new Direction ( "WNW", new BigDecimal ( "286.88" ), new BigDecimal ( "298.12" ) ) );
    directions.add ( new Direction ( "NWbW", new BigDecimal ( "298.13" ), new BigDecimal ( "309.37" ) ) );
    directions.add ( new Direction ( "NW", new BigDecimal ( "309.38" ), new BigDecimal ( "320.62" ) ) );
    directions.add ( new Direction ( "NWbN", new BigDecimal ( "320.63" ), new BigDecimal ( "331.87" ) ) );
    directions.add ( new Direction ( "NNW", new BigDecimal ( "331.88" ), new BigDecimal ( "343.12" ) ) );
    directions.add ( new Direction ( "NbW", new BigDecimal ( "343.13" ), new BigDecimal ( "354.37" ) ) );
    directions.add ( new Direction ( "N", new BigDecimal ( "354.38" ), new BigDecimal ( "360" ) ) );
  }

  @Override
  public String getDescriptionForDegrees ( BigDecimal deg ) {
    for ( Direction direction : directions ) {
      if ( deg.compareTo ( direction.getMin () ) >= 0 && deg.compareTo ( direction.getMax () ) <= 0 ) {
        return direction.getLabel ();
      }
    }
    return null;
  }

  private static class Direction {
    private BigDecimal min;
    private BigDecimal max;
    private String label;

    public Direction ( String label, BigDecimal min, BigDecimal max ) {
      this.min = min;
      this.max = max;
      this.label = label;
    }

    public BigDecimal getMin () {
      return min;
    }

    public BigDecimal getMax () {
      return max;
    }

    public String getLabel () {
      return label;
    }
  }
}
