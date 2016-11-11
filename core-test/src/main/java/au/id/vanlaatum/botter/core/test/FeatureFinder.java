package au.id.vanlaatum.botter.core.test;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class FeatureFinder {
  public static String[] getRequiredFeatures ( String url, String... features ) {
    try {
      final List<String> requiredFeatures = new ArrayList<> ();
      final List<String> requestedFeatures = Arrays.asList ( features );
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance ();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder ();
      Document doc = dBuilder.parse ( url );
      XPathFactory xPathfactory = XPathFactory.newInstance ();
      XPath xpath = xPathfactory.newXPath ();

      for ( String feature : features ) {
        final XPathExpression expression = xpath.compile ( format ( "//feature[@name='%s']/feature", feature ) );
        NodeList nodes = (NodeList) expression.evaluate ( doc, XPathConstants.NODESET );
        for ( int i = 0; i < nodes.getLength (); i++ ) {
          if ( !requestedFeatures.contains ( nodes.item ( i ).getTextContent () ) ) {
            requiredFeatures.add ( nodes.item ( i ).getTextContent () );
          }
        }
      }

      return requiredFeatures.toArray ( new String[requiredFeatures.size ()] );
    } catch ( Throwable ex ) {
      throw new RuntimeException ( ex );
    }
  }
}
