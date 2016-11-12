package au.id.vanlaatum.botter.core.test;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathVariableResolver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeatureFinder {
  private FeatureFinder () {
  }

  public static String[] getRequiredFeatures ( String url, String... features ) {
    try {
      final List<String> requiredFeatures = new ArrayList<> ();
      final List<String> requestedFeatures = Arrays.asList ( features );
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance ();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder ();
      Document doc = dBuilder.parse ( url );
      XPathFactory xPathfactory = XPathFactory.newInstance ();
      XPath xpath = xPathfactory.newXPath ();

      for ( final String feature : features ) {
        xpath.setXPathVariableResolver ( new XPathVariableResolver () {
          @Override
          public Object resolveVariable ( QName variableName ) {
            return "feature".equals ( variableName.toString () ) ? feature : null;
          }
        } );
        final XPathExpression expression = xpath.compile ( "//feature[@name=$feature]/feature" );
        NodeList nodes = (NodeList) expression.evaluate ( doc, XPathConstants.NODESET );
        for ( int i = 0; i < nodes.getLength (); i++ ) {
          final String requiredFeature = nodes.item ( i ).getTextContent ().trim ();
          if ( !requestedFeatures.contains ( requiredFeature ) ) {
            if ( requiredFeature.startsWith ( "botter" ) ) {
              throw new RuntimeException ( "Attempted to add " + requiredFeature );
            }
            requiredFeatures.add ( requiredFeature );
          }
        }
        if ( nodes.getLength () == 0 ) {
          throw new RuntimeException ( "Failed to find feature " + feature + " in " + url );
        }
      }

      System.out.println ( "Required features: " + requiredFeatures );

      return requiredFeatures.toArray ( new String[requiredFeatures.size ()] );
    } catch ( Throwable ex ) {
      throw new RuntimeException ( ex );
    }
  }
}
