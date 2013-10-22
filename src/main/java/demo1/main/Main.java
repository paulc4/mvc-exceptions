package demo1.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.AbstractEnvironment;

/**
 * Main entry point for our application using Spring Boot. It can be run as an
 * executable or as a standard war file.
 * <p>
 * Details of annotations used:
 * <ul>
 * <li><tt>@EnableAutoConfiguration</tt>: makes Spring Boot setup its defaults.
 * <li><tt>@ComponentScan</tt>: Scan for @Component classes, including @Configuration
 * classes.
 * <li><tt>@ImportResource</tt>: Import Spring XML configuration files.
 * </ul>
 * 
 * @author Paul Chapman
 */
@EnableAutoConfiguration
@ComponentScan("demo1.web")
// Find controllers
public class Main extends SpringBootServletInitializer {

	// Controller profile - exceptions handled by methods on the
	// ExceptionController class.
	public static final String CONTROLLER_PROFILE = "controller";

	// Global profile - exceptions handled via a @ControllerAdvice
	// instance.
	public static final String GLOBAL_PROFILE = "global";

	// Set true for Global exception handling profile
	protected boolean global = true;

	/**
	 * We are using the constructor to perform some useful initialisations:
	 * <ol>
	 * <li>Set the Spring Profile to use 'controller' or 'global' which in turn
	 * selects how exceptions will be handled. Profiles are a Spring feature
	 * from V3.1 onwards.
	 * <li>Disable Thymeleaf caching. See {@link ThymeleafAutoConfiguration} to
	 * see how this is used.
	 * </ol>
	 */
	public Main() {
		// Set active profiles - in this case, just one.
		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME,
				global ? GLOBAL_PROFILE : CONTROLLER_PROFILE);

		// Disable caching - during development if a page is changed, the
		// changes can be seen next time it is rendered. Should be true in
		// production for efficiency.
		System.setProperty("spring.thymeleaf.cache", "false");

		// Setup JSP. The "support" error view shows how to embed exception
		// information inside an HTML comment so it is only visible by looking
		// at the page source. We are only using JSP for this one page because
		// Thymeleaf can't yet generate dynamic content inside comments.
		System.setProperty("spring.view.prefix", "/WEB-INF/");
		System.setProperty("spring.view.suffix", ".jsp");
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
		SpringApplication.run(Main.class, args);
	}

	/**
	 * Tell Spring Boot that we are the entry-point class when running as a
	 * Servlet application. This method is only used when running in a
	 * container.
	 */
	@Override
	protected void configure(SpringApplicationBuilder application) {
		application.sources(Main.class);
	}

}
