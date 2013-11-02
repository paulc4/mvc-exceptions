package demo1.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * Main entry point for our application using Spring Boot. It can be run as an
 * executable or as a standard war file.
 * <p>
 * Details of annotations used:
 * <ul>
 * <li><code>@EnableAutoConfiguration</code>: makes Spring Boot setup its
 * defaults.
 * <li><code>@ComponentScan</code>: Scan for @Component classes, including @Configuration
 * classes.
 * <li><code>@ImportResource</code>: Import Spring XML configuration file(s).
 * </ul>
 * 
 * @author Paul Chapman
 */
@EnableAutoConfiguration
@ComponentScan("demo1.web")
@ImportResource("classpath:mvc-configuration.xml")
public class Main extends SpringBootServletInitializer {

	/**
	 * Options for setting up a <code>SimpleMappingExceptionResolver</code>.
	 * 
	 * @see Main#smerConfig
	 */
	public enum ResolverSetup {
		NONE("none"), // Do not create
		JAVA(Profiles.JAVA_CONFIG_PROFILE), // create using Java configuration
		XML(Profiles.XML_CONFIG_PROFILE); // create using XML

		String value;

		ResolverSetup(String value) {
			this.value = value;
		}

		public String toString() {
			return value;
		}
	}

	/**
	 * Set true for Global exception handling profile. Value = <b>{@value} </b>
	 * 
	 * @see Profiles
	 */
	public static final boolean global = false;

	/**
	 * Is a <code>SimpleMappingExceptionResolver</code> to be created and if so
	 * how? Set to 'NONE' to not define one, to 'JAVA' to configure using Java
	 * Configuration or 'XML' for XML configuration.
	 * 
	 * @see Profiles
	 */
	public static final ResolverSetup smerConfig = ResolverSetup.NONE;

	// Local logger
	protected Logger logger;

	// Holds Spring Boot configuration properties
	protected Properties props = new Properties();

	/**
	 * Retrieve requested Profiles. Depends on the value of {@link #global} and
	 * {@link #smerConfig}.
	 * 
	 * @return Comma-separated list of profiles, forced to upper-case.
	 */
	public static String getProfiles() {
		return (global ? Profiles.GLOBAL_PROFILE : Profiles.CONTROLLER_PROFILE)
				+ ", " + smerConfig;
	}

	/**
	 * We are using the constructor to perform some useful initialisations:
	 * <ol>
	 * <li>Set the Spring Profile to use 'controller' or 'global' which in turn
	 * selects how exceptions will be handled. Profiles are a Spring feature
	 * from V3.1 onwards.
	 * <li>Disable Thymeleaf caching. See {@link ThymeleafAutoConfiguration} to
	 * see how this is used.</li>
	 * <li>Enable DEBUG level logging so you can see Spring MVC as its working.</li>
	 * </ol>
	 */
	public Main() {

		logger = LoggerFactory.getLogger(getClass());
		logger.info("Application starting ");

		// Disable caching - during development if a page is changed, the
		// changes can be seen next time it is rendered. Should be 'true' in
		// production for efficiency.
		props.setProperty("spring.thymeleaf.cache", "false");

		// Setup JSP by defining the prefix and suffix properties of the
		// InternalResourceViewResolver.
		//
		// NOTE: The "support" error view shows how to embed exception
		// information inside an HTML comment so it is only visible by looking
		// at the page source. We are only using JSP for this one page because
		// Thymeleaf can't (yet) generate dynamic content inside comments.
		props.setProperty("spring.view.prefix", "/WEB-INF/");
		props.setProperty("spring.view.suffix", ".jsp");

		// Spring boot assumes the fallback error page maps to /error. Set this
		// property to specify an alternative mapping. If using a
		// SimpleMappingExceptionResolver, make sure it's defaultErrorView
		// corresponds to the same page (see ErrorMvcAutoConfiguration).
		//
		props.setProperty("error.path", "/error");

		// Set to false to turn-off Spring Boot's error page. Unhandled
		// exceptions will be handled by container in the usual way.
		props.setProperty("error.whitelabel.enabled", "true");

		// Enable internal logging for Spring MVC
		LoggingUtilities.setLogLevel("org.springframework.web", "DEBUG");
	}

	/**
	 * Back to the future: run the application as a Java application and it will
	 * pick up a container Tomcat, Jetty) automatically if present. Pulls in
	 * Tomcat by default, running in embedded mode.
	 * <p>
	 * This application can also run as a traditional war file because it
	 * extends <code>SpringBootServletInitializer</code> as well.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// Create an instance and invoke run(); Allows the contructor to perform
		// initialisation regardless of whether we are running as an application
		// or in a container.
		new Main().runAsJavaApplication(args);
	}

	/**
	 * Run the application using Spring Boot. <code>SpringApplication.run</code>
	 * tells Spring Boot to use this class as the initialiser for the whole
	 * application (via the class annotations above). This method is only used
	 * when running as a Java application.
	 * 
	 * @param args
	 *            Any command line arguments.
	 */
	protected void runAsJavaApplication(String[] args) {
		SpringApplicationBuilder application = new SpringApplicationBuilder();
		configure(application);
		application.run(args);
		logger.info("Go to this URL: http://localhost:8080/");
	}

	/**
	 * Configure the application usign the supplied builder. This method is
	 * invoked automatically when running in a container and explicitly by
	 * {@link #runAsJavaApplication(String[])}.
	 * 
	 * @param application
	 *            Spring Boot application builder.
	 */
	@Override
	protected void configure(SpringApplicationBuilder application) {
		application.sources(Main.class);

		// Set active profiles.
		// @formatter:off
		List<String> profiles = new ArrayList<String>();
		profiles.add(global ? Profiles.GLOBAL_PROFILE
								: Profiles.CONTROLLER_PROFILE);

		if (smerConfig != ResolverSetup.NONE)
			profiles.add(smerConfig == ResolverSetup.JAVA ?
		        Profiles.JAVA_CONFIG_PROFILE : Profiles.XML_CONFIG_PROFILE);

		logger.info("Spring Boot configuration: profiles = " + profiles);
		application.profiles(profiles.toArray(new String[profiles.size()]));
		// @formatter:on

		// Set additional properties. Note: this API does not exist in 0.5.0.M5
		// or earlier.
		logger.info("Spring Boot configuratoon: properties = " + props);
		application.properties(props); // New API

		// NOTE: If you want to stick with M5, set these properties as System
		// properties by iterating over them and using System.setProperty(). DO
		// NOT use system.setProperties() or you will lose all the default
		// properties and get some very weird errors!
	}
}
