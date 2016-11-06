package au.id.vanlaatum.botter.transport.slack;

import au.id.vanlaatum.botter.api.BotFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.UrlProvisionOption;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.lang.String.format;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

@RunWith ( PaxExam.class )
public class SlackTransportIT {
  @Inject
  private BundleContext bundleContext;
  @Inject
  private BotFactory botFactory;
  @Inject
  private LogService log;

  public UrlProvisionOption bundleLink ( String name ) throws IOException {
    final Path path = Paths.get ( format ( "target/test-classes/%s.link", name ) ).toAbsolutePath ();
    final List<String> strings = Files.readAllLines ( path, Charset.defaultCharset () );
    return bundle ( strings.get ( 0 ) );
  }

  @Configuration
  public Option[] configure () throws IOException, URISyntaxException {
    String version = mavenBundle ( "au.id.vanlaatum.botter", "slack-transport" ).versionAsInProject ().getURL ().split ( "/" )[2];
    return options (
        cleanCaches (),
        junitBundles (),
        karafOptions (),
        bundleLink ( "au.id.vanlaatum.botter.core" ),
        bundle ( "file:" + PathUtils.getBaseDir () + "/target/slack-transport-" + version + ".jar" )
    );
  }

  private Option karafOptions () throws URISyntaxException {
    return composite (
        keepRuntimeFolder (),
        editConfigurationFilePut ( "etc/org.apache.karaf.management.cfg", "rmiRegistryPort", "2000" ),
        editConfigurationFilePut ( "etc/org.apache.karaf.management.cfg", "rmiServerPort", "44445" ),
        editConfigurationFilePut ( "etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", "8182" ),
        composite ( editConfigurationFilePut ( "etc/org.ops4j.pax.logging.cfg", Paths.get ( "target/test-classes/logging.properties" ).toFile () ) ),
        logLevel ( LogLevelOption.LogLevel.WARN ),
        karafDistributionConfiguration ().frameworkUrl ( maven ( "org.apache.karaf", "apache-karaf" ).versionAsInProject ().type ( "tar.gz" ) )
            .unpackDirectory ( new File ( "target/exam" ) ).useDeployFolder ( false ),
        configureConsole ().ignoreRemoteShell ().ignoreLocalConsole (),
        features ( maven ( "org.apache.karaf.features", "standard" ).classifier ( "features" ).type ( "xml" ).versionAsInProject (), "aries-blueprint",
            "aries-proxy" ),
        features ( maven ( "org.ops4j.pax.cdi", "pax-cdi-features" ).classifier ( "features" ).type ( "xml" ).versionAsInProject (), "pax-cdi" ),
        features ( "file:" + PathUtils.getBaseDir () + "/../features/target/classes/features-test.xml", "test-deps" ),
        features ( "file:" + PathUtils.getBaseDir () + "/../features/target/classes/features.xml", "tyrus", "commons-lang", "commons-io" )
    );
  }

  @Test
  public void transportTest () {

  }
}
