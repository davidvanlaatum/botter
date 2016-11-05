package au.id.vanlaatum.botter.connector.whereis.impl;

import au.id.vanlaatum.botter.api.BotFactory;
import au.id.vanlaatum.botter.api.KeyWordProcessor;
import au.id.vanlaatum.botter.connector.mock.transport.api.MessageAssert;
import au.id.vanlaatum.botter.connector.mock.transport.api.MockTransportConfigurator;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
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
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.jdbc.DataSourceFactory;
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
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

@RunWith ( PaxExam.class )
public class WhereIsKeywordIT {

  private static org.osgi.service.cm.Configuration config;
  @Inject
  protected ConfigurationAdmin configAdmin;
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
        systemProperty ( "hibernate.hbm2ddl.auto" ).value ( "verify" ),
        cleanCaches (),
//        systemProperty("pax.exam.invoker").value("junit"),
        junitBundles (),
//        systemProperty ( "felix.log.level" ).value ( "4" ),
//        systemProperty ( "org.ops4j.pax.logging.DefaultServiceLog.level" ).value ( "INFO" ),
//        mavenBundle ( "org.ops4j.pax.logging", "pax-logging-api" ).versionAsInProject ().startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
//        linkBundle ( "org.ops4j.pax.swissbox.property" ).startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
//        linkBundle ( "org.ops4j.pax.url.commons" ).startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
//        linkBundle ( "org.ops4j.pax.url.classpath" ).startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
//        linkBundle ( "org.ops4j.pax.url.link" ).startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
//        linkBundle ( "org.ops4j.pax.url.reference" ).startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
//        linkBundle ( "org.ops4j.pax.url.mvn" ).startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
        /*mavenBundle ( "org.apache.felix", "org.apache.felix.log" ).versionAsInProject ().startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
        mavenBundle ( "org.apache.felix", "org.apache.felix.coordinator" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.felix", "org.apache.felix.eventadmin" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.aries.blueprint", "org.apache.aries.blueprint.core" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.aries.blueprint", "org.apache.aries.blueprint.api" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.aries", "org.apache.aries.util" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.aries.quiesce", "org.apache.aries.quiesce.api" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.aries.proxy", "org.apache.aries.proxy.api" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "javax.el", "javax.el-api" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "javax.interceptor", "javax.interceptor-api" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "javax.enterprise", "cdi-api" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.ops4j.pax.cdi", "pax-cdi-api" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.commons", "commons-lang3" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "com.fasterxml.jackson.core", "jackson-databind" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "com.fasterxml.jackson.core", "jackson-annotations" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "com.fasterxml.jackson.core", "jackson-core" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.httpcomponents", "httpclient-osgi" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "commons-logging", "commons-logging" ).versionAsInProject ().startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
        mavenBundle ( "commons-codec", "commons-codec" ).versionAsInProject ().startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
        mavenBundle ( "org.apache.httpcomponents", "httpcore-osgi" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.antlr", "antlr4-runtime" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.ops4j.pax.jdbc", "pax-jdbc-pool-dbcp2" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.commons", "commons-pool2" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.commons", "commons-dbcp2" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.geronimo.specs", "geronimo-jta_1.1_spec" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.ops4j.pax.jdbc", "pax-jdbc-pool-common" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "commons-io", "commons-io" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "javax.transaction", "javax.transaction-api" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.servicemix.specs", "org.apache.servicemix.specs.jaxb-api-2.2" ).versionAsInProject ()
            .startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.servicemix.specs", "org.apache.servicemix.specs.stax-api-1.0" ).versionAsInProject ()
            .startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.geronimo.specs", "geronimo-activation_1.1_spec" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.servicemix.bundles", "org.apache.servicemix.bundles.jasypt" ).versionAsInProject ()
            .startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.osgi", "org.osgi.compendium" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.servicemix.bundles", "org.apache.servicemix.bundles.javax-inject" ).versionAsInProject ()
            .startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.geronimo.specs", "geronimo-osgi-locator" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.geronimo.specs", "geronimo-osgi-registry" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.aries.blueprint", "org.apache.aries.blueprint.cm" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.apache.aries.proxy", "org.apache.aries.proxy.impl" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.ops4j.pax.jdbc", "pax-jdbc-config" ).versionAsInProject ().startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.hibernate.javax.persistence", "hibernate-jpa-2.1-api" ).versionAsInProject ().startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
        mavenBundle ( "org.javassist", "javassist" ).versionAsInProject ().startLevel ( 3 ),
        mavenBundle ( "org.jboss.logging", "jboss-logging" ).versionAsInProject ().startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
        mavenBundle ( "org.apache.servicemix.bundles", "org.apache.servicemix.bundles.dom4j" ).versionAsInProject ().startLevel ( 3 ),
        mavenBundle ( "org.apache.servicemix.bundles", "org.apache.servicemix.bundles.antlr" ).versionAsInProject ().startLevel ( 3 ),
        mavenBundle ( "org.hibernate", "hibernate-core" ).versionAsInProject ().startLevel ( 3 ),
        mavenBundle ( "org.hibernate", "hibernate-osgi" ).versionAsInProject ().startLevel ( 3 ),
        mavenBundle ( "org.jboss", "jandex" ).versionAsInProject ().startLevel ( 3 ),
        mavenBundle ( "com.fasterxml", "classmate" ).versionAsInProject ().startLevel ( 3 ),
        mavenBundle ( "org.hibernate.common", "hibernate-commons-annotations" ).versionAsInProject ().startLevel ( 3 ),
        mavenBundle ( "org.apache.felix", "org.apache.felix.configadmin" ).versionAsInProject ().startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
        mavenBundle ( "org.apache.aries.jpa", "org.apache.aries.jpa.api" ).versionAsInProject ().startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
        mavenBundle ( "org.apache.aries.jpa", "org.apache.aries.jpa.blueprint" ).versionAsInProject ().startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
        mavenBundle ( "org.apache.aries.jpa", "org.apache.aries.jpa.support" ).versionAsInProject ().startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
        mavenBundle ( "org.apache.aries.jpa", "org.apache.aries.jpa.container" ).versionAsInProject ().startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
        mavenBundle ( "org.apache.servicemix.bundles", "org.apache.servicemix.bundles.woodstox" ).versionAsInProject ()
            .startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
        mavenBundle ( "org.apache.aries.transaction", "org.apache.aries.transaction.manager" ).versionAsInProject ().startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
        mavenBundle ( "org.apache.aries.transaction", "org.apache.aries.transaction.blueprint" ).versionAsInProject ()
            .startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
        mavenBundle ( "joda-time", "joda-time" ).versionAsInProject ().startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
        mavenBundle ( "org.apache.derby", "derby" ).startLevel ( START_LEVEL_DEFAULT_PROVISION ),
        mavenBundle ( "org.ops4j.pax.jdbc", "pax-jdbc-derby" ).startLevel ( START_LEVEL_DEFAULT_PROVISION ),*/
//        bundle ( "reference:file:" + PathUtils.getBaseDir () + "/target/classes/" ).startLevel ( START_LEVEL_TEST_BUNDLE ),
//        vmOption ( "-Xrunjdwp:transport=dt_socket,server=n,suspend=y,address=localhost:5005" ),
//        mavenBundle("org.ops4j.pax.exam","pax-exam-link-mvn").versionAsInProject (),
        karafOptions (),
//        mavenBundle ( "org.apache.aries.transaction", "org.apache.aries.transaction.wrappers", "1.0.0" ),
//        mavenBundle ( "org.apache.aries.jpa", "org.apache.aries.jpa.api", "1.0.2" ),
//        mavenBundle ( "org.apache.aries.jpa", "org.apache.aries.jpa.container", "2.4.0" ),
//        mavenBundle ( "org.apache.geronimo.specs", "geronimo-jta_1.1_spec", "1.1.1" ),
//        linkBundle ( "org.ops4j.pax.url.wrap" ).startLevel ( START_LEVEL_SYSTEM_BUNDLES ),
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
    return CoreOptions.composite (
        keepRuntimeFolder (),
        editConfigurationFilePut ( "etc/org.apache.karaf.management.cfg", "rmiRegistryPort", "2000" ),
        editConfigurationFilePut ( "etc/org.apache.karaf.management.cfg", "rmiServerPort", "44445" ),
        editConfigurationFilePut ( "etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", "8182" ),
        logLevel ( LogLevelOption.LogLevel.WARN ),
        karafDistributionConfiguration ().frameworkUrl ( maven ( "org.apache.karaf", "apache-karaf" ).versionAsInProject ().type ( "tar.gz" ) )
            .unpackDirectory ( new File ( "target/exam" ) ).useDeployFolder ( false ),
        configureConsole ().ignoreRemoteShell ().ignoreLocalConsole (),
        features ( maven ( "org.apache.karaf.features", "standard" ).classifier ( "features" ).type ( "xml" ).versionAsInProject (), "aries-blueprint",
            "aries-proxy" ),
        features ( maven ( "org.apache.karaf.features", "enterprise" ).classifier ( "features" ).type ( "xml" ).versionAsInProject (), "jpa",
            "jndi", "jdbc", "transaction", "hibernate" ),
        features ( maven ( "org.ops4j.pax.cdi", "pax-cdi-features" ).classifier ( "features" ).type ( "xml" ).versionAsInProject (), "pax-cdi" ),
        features ( maven ( "org.ops4j.pax.jdbc", "pax-jdbc-features" ).classifier ( "features" ).type ( "xml" ).versionAsInProject (), "pax-jdbc-derby",
            "pax-jdbc-config" ),
        features ( "file:" + PathUtils.getBaseDir () + "/../features/target/classes/features-test.xml", "test-deps" ),
        features ( "file:" + PathUtils.getBaseDir () + "/../features/target/classes/features.xml", "antlr4", "joda-time" )
    );
  }

  @Before
  public void createConfigForDataSource () throws Exception {
    if ( config == null ) {
      createConfigForLogging ();
      config = configAdmin.createFactoryConfiguration ( "org.ops4j.datasource", null );
      Dictionary<String, String> props = new Hashtable<> ();
      props.put ( DataSourceFactory.OSGI_JDBC_DRIVER_NAME, "derby" );
      props.put ( DataSourceFactory.JDBC_URL, "jdbc:derby:memory:TEST1;create=true" );
      props.put ( "dataSourceName", "botter-whereis" );
      props.put ( "liquibase", "true" );
      config.update ( props );
      mockTransportConfigurator.start ();
    }
  }

  @After
  public void end () throws IOException {
    mockTransportConfigurator.stop ();
  }

  public void createConfigForLogging () throws IOException {
    org.osgi.service.cm.Configuration logConfig = configAdmin.getConfiguration ( "org.ops4j.pax.logging", null );
    Dictionary<String, String> props = new Hashtable<> ();
    props.put ( "log4j.logger.au.id.vanlaatum.botter", "DEBUG, stdout" );
    props.put ( "log4j.appender.stdout", "org.apache.log4j.ConsoleAppender" );
    props.put ( "log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout" );
    props.put ( "log4j.appender.stdout.layout.ConversionPattern", "%d{ISO8601} | %-5.5p | %-16.16t | %c | %m%n" );
    logConfig.update ( props );
  }

  private <T> void waitForService ( Class<T> clazz, String filter ) throws InvalidSyntaxException, InterruptedException {
    waitForService ( MessageFormat.format ( "(&({0}={1}){2})", Constants.OBJECTCLASS, clazz.getName (), filter ) );
  }

  private void waitForService ( String filter ) throws InvalidSyntaxException, InterruptedException {
    final Filter flt = FrameworkUtil.createFilter ( filter );
    ServiceTracker<Object, Object> tracker = new ServiceTracker<> ( bundleContext, flt, null );
    tracker.open ();
    try {
      assertNotNull ( "timeout waiting for service", tracker.waitForService ( 10000 ) );
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
      messageAssert.assertMessage ( MessageAssert.MessageResponseType.REPLY, CoreMatchers.startsWith ( "ok I have set your location to not in between " ) );

      messageAssert = mockTransportConfigurator.injectMessage ( "where is david" ).from ( "joe" ).channel ( "abc1" ).send ();
      messageAssert.assertMessage ( MessageAssert.MessageResponseType.REPLY, "david is not in" );
    } catch ( Throwable e ) {
      log.log ( LogService.LOG_ERROR, null, e );
      throw e;
    }
  }
}
