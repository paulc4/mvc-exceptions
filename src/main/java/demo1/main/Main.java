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
 * <li><tt>@EnableAutoConfiguration</tt>: makes Spring Boot setup its defaults.
 * <li><tt>@ComponentScan</tt>: Scan for @Component classes, including @Configuration
 * classes.
 * <li><tt>@ImportResource</tt>: Import Spring XML configuration file(s).
 * </ul>
 * 
 * @author Paul Chapman
 */
@EnableAutoConfiguration
@ComponentScan("demo1.web")
@ImportResource("classpath:mvc-configuration.xml")
public class Main extends SpringBootServletInitializer {

	/**
	 * Set true for Global exception handling profile. Value = <b>{@value} </b>
	 * 
	 * @see Profiles
	 */
	public static final boolean global = false;

	/**
	 * Set to true for Java Configuration or false for XML configuration. Value
	 * = <b>{@value} </b>. This profile is only used if {@link #global} is
	 * <tt>true</tt>.
	 * 
	 * @see Profiles
	 */
	public static final boolean useJavaConfig = true;

	// Local logger
	protected Logger logger;

	// Holds Spring Boot configuration properties
	protected Properties props = new Properties();

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
		logger.info("Application starting");

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

		// Enable internal logging for Spring MVC
		LoggingUtilities.setLogLevel("org.springframework.web", "DEBUG");
	}

	/**
	 * Back to the future: run the application as a Java application and it will
	 * pick up a container Tomcat, Jetty) automatically if present. Pulls in
	 * Tomcat by default, running in embedded mode.
	 * <p>
	 * This application can also run as a traditional war file because it
	 * extends <tt>SpringBootServletInitializer</tt> as well.
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
	 * Run the application using Spring Boot. <tt>SpringApplication.run</tt>
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
	 * Tell Spring Boot that we are the entry-point class when running as a
	 * Servlet application. This method is only used when running in a
	 * container.
	 */
	@Override
	protected void configure(SpringApplicationBuilder application) {
		application.sources(Main.class);

		// Set active profiles. Note that XML_CONFIG and JAVA_CONFIG are only
		// used when GLOBAL_PROFILE is also active.
		application.profiles(global ? Profiles.GLOBAL_PROFILE
				: Profiles.CONTROLLER_PROFILE,
				useJavaConfig ? Profiles.JAVA_CONFIG_PROFILE
						: Profiles.XML_CONFIG_PROFILE);

		// Set additional properties. Ugly cast because Properties is a
		// Map<Object,Object>. This API does not exist in 0.5.0.M5 or earlier.
		//    application.properties((Map<String, Object>) ((Map) props));

		// Convert our properties to a list of Strings
		List<String> strings = new ArrayList<String>();
		for (Map.Entry<Object, Object> entry : props.entrySet())
			strings.add((String) entry.getKey() + "=" + entry.getValue());

 		application.properties(strings.toArray(new String[props.size()]));

		// Old code (0.5.0.M5 or earlier)
		// Iterate over the properties and use System.setProperty() instead.
		// Do NOT use System.setProperties(props) or you will lose all the
		// default System properties and get some very weird errors!!
	}
}
