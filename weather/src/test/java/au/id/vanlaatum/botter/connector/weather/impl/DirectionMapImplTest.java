package au.id.vanlaatum.botter.connector.weather.impl;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class DirectionMapImplTest {
  @Test
  public void getDescriptionForDegrees () throws Exception {
    DirectionMapImpl impl = new DirectionMapImpl ();
    assertEquals ( "N", impl.getDescriptionForDegrees ( new BigDecimal ( 0 ) ) );
    assertEquals ( "N", impl.getDescriptionForDegrees ( new BigDecimal ( 360 ) ) );
    assertEquals ( "N", impl.getDescriptionForDegrees ( new BigDecimal ( 359 ) ) );
    assertEquals ( "N", impl.getDescriptionForDegrees ( new BigDecimal ( 5 ) ) );
    assertEquals ( "NbE", impl.getDescriptionForDegrees ( new BigDecimal ( 10 ) ) );
    assertEquals ( "NNE", impl.getDescriptionForDegrees ( new BigDecimal ( 20 ) ) );
    assertEquals ( "NE", impl.getDescriptionForDegrees ( new BigDecimal ( 40 ) ) );
  }

}
