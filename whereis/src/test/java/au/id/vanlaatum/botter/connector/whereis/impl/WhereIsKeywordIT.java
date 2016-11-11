package au.id.vanlaatum.botter.connector.whereis.impl;

import au.id.vanlaatum.botter.api.BotFactory;
import au.id.vanlaatum.botter.api.KeyWordProcessor;
import au.id.vanlaatum.botter.connector.mock.transport.api.MessageAssert;
import au.id.vanlaatum.botter.connector.mock.transport.api.MockTransportConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.UrlProvisionOption;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;

import static au.id.vanlaatum.botter.core.test.FeatureFinder.getRequiredFeatures;
import static java.lang.String.format;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

@RunWith ( PaxExam.class )
public class WhereIsKeywordIT {

  @Inject
  private BundleContext bundleContext;
  @Inject
  private BotFactory botFactory;
  @Inject
  private LogService log;
  @Inject
  private MockTransportConfigurator mockTransportConfigurator;

  public UrlProvisionOption bundleLink ( String name ) throws IOException {
    final Path path = Paths.get ( format ( "target/test-classes/%s.link", name ) ).toAbsolutePath ();
    final List<String> strings = Files.readAllLines ( path, Charset.defaultCharset () );
    return bundle ( strings.get ( 0 ) );
  }

  @Configuration
  public Option[] configure () throws IOException, URISyntaxException {
    String version = mavenBundle ( "au.id.vanlaatum.botter", "whereis" ).versionAsInProject ().getURL ().split ( "/" )[2];
    return options (
        cleanCaches (),
        junitBundles (),
        karafOptions (),
        wrappedBundle ( maven ( "org.hamcrest", "hamcrest-library" ).versionAsInProject () )
            .instructions ( "Fragment-Host=org.ops4j.pax.tipi.hamcrest.core" )
            .noStart (),
        mavenBundle ( "org.apache.commons", "commons-lang3" ).versionAsInProject (),
        mavenBundle ( "org.liquibase", "liquibase-osgi" ).versionAsInProject (),
        mavenBundle ( "org.yaml", "snakeyaml" ).versionAsInProject (),
        bundleLink ( "au.id.vanlaatum.botter.core" ),
        bundleLink ( "au.id.vanlaatum.botter.mock-transport" ),
        bundleLink ( "au.id.vanlaatum.botter.whereis-modal" ),
        bundle ( "file:" + PathUtils.getBaseDir () + "/target/whereis-" + version + ".jar" )
    );
  }

  private Option karafOptions () throws URISyntaxException {
    final String featuresPath = "file:" + PathUtils.getBaseDir () + "/../features/target/classes/";
    return composite (
        keepRuntimeFolder (),
        editConfigurationFilePut ( "etc/org.apache.karaf.management.cfg", "rmiRegistryPort", "2000" ),
        editConfigurationFilePut ( "etc/org.apache.karaf.management.cfg", "rmiServerPort", "44445" ),
        editConfigurationFilePut ( "etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", "8182" ),
        composite ( editConfigurationFilePut ( "etc/org.ops4j.pax.logging.cfg", Paths.get ( "target/test-classes/logging.properties" ).toFile () ) ),
        composite ( editConfigurationFilePut ( "etc/org.ops4j.datasource-whereis.cfg", Paths.get ( "target/test-classes/datasource.properties" ).toFile () ) ),
        logLevel ( LogLevelOption.LogLevel.INFO ),
        karafDistributionConfiguration ().frameworkUrl ( maven ( "org.apache.karaf", "apache-karaf" ).versionAsInProject ().type ( "tar.gz" ) )
            .unpackDirectory ( new File ( "target/exam" ) ).useDeployFolder ( false ),
        configureConsole ().ignoreRemoteShell ().ignoreLocalConsole (),
        features ( featuresPath + "features-test.xml", "test-deps" ),
        features ( featuresPath + "features.xml", getRequiredFeatures ( featuresPath + "features.xml", "botter", "botter-whereis" ) )
    );
  }

  @Before
  public void createConfigForDataSource () throws Exception {
    mockTransportConfigurator.start ();
  }

  @After
  public void end () throws IOException {
    mockTransportConfigurator.stop ();
  }

  private <T> void waitForService ( Class<T> clazz, String filter ) throws InvalidSyntaxException, InterruptedException {
    waitForService ( MessageFormat.format ( "(&({0}={1}){2})", Constants.OBJECTCLASS, clazz.getName (), filter ) );
  }

  private void waitForService ( String filter ) throws InvalidSyntaxException, InterruptedException {
    final Filter flt = FrameworkUtil.createFilter ( filter );
    ServiceTracker<Object, Object> tracker = new ServiceTracker<> ( bundleContext, flt, null );
    tracker.open ();
    try {
      assertNotNull ( "timeout waiting for service", tracker.waitForService ( 60000 ) );
    } finally {
      tracker.close ();
    }
  }

  @Test
  public void keywordTest () throws Exception {
    try {
      assertNotNull ( bundleContext );
      waitForService ( "(osgi.jndi.service.name=botter-whereis)" );
      waitForService ( KeyWordProcessor.class, "(osgi.service.blueprint.compname=WhereIsKeyword)" );
      assertNotNull ( botFactory );
      botFactory.cleanCaches ();
      mockTransportConfigurator.addUser ( "joe" ).build ();
      mockTransportConfigurator.addUser ( "david" ).build ();
      mockTransportConfigurator.addChannel ( "abc1" ).build ();
      MessageAssert messageAssert = mockTransportConfigurator.injectMessage ( "where is david" ).from ( "joe" ).channel ( "abc1" ).send ();
      messageAssert.assertMessage ( MessageAssert.MessageResponseType.REPLY, "I have no idea where david is" );

      messageAssert = mockTransportConfigurator.injectMessage ( "not in today" ).from ( "david" ).channel ( "abc1" ).send ();
      messageAssert.assertMessage ( MessageAssert.MessageResponseType.REPLY, startsWith ( "ok I have set your location to not in between " ) );

      messageAssert = mockTransportConfigurator.injectMessage ( "where is david" ).from ( "joe" ).channel ( "abc1" ).send ();
      messageAssert.assertMessage ( MessageAssert.MessageResponseType.REPLY, "david is not in" );
    } catch ( Throwable e ) {
      log.log ( LogService.LOG_ERROR, null, e );
      throw e;
    }
  }
}
