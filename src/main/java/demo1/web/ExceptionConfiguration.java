package demo1.web;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * Setup for exception handling using a {@link SimpleMappingExceptionResolver}
 * bean.  Note that the {@link #defaultErrorView()} method is only needed
 * because we are using Spring Boot.  If you are happy to use Spring Boot's
 * default error view or not using Spring Boot at all, it is not needed.
 *
 * @author Paul Chapman 
 */
@Configuration
@Profile("global")
public class ExceptionConfiguration {
	
	protected Logger logger;

	public ExceptionConfiguration() {
		logger = LoggerFactory.getLogger(getClass());
	}
	
	/**
	 * Setup the classic SimpleMappingExceptionResolver. This provides useful
	 * defaults for logging and handling exceptions. It has been part of Spring
	 * MVC since Spring V2 and you will probably find most existing Spring MVC
	 * applications are using it.
	 * 
	 * @return The new resolver.
	 */
	@Bean(name="simpleMappingExceptionResolver")
	public SimpleMappingExceptionResolver createSimpleMappingExceptionResolver() {
		logger.info("Creating SimpleMappingExceptionResolver");
		SimpleMappingExceptionResolver r = new SimpleMappingExceptionResolver();

		Properties mappings = new Properties();
		mappings.setProperty("DatabaseException", "databaseError");
		mappings.setProperty("InvalidCreditCardException", "creditCardError");

		r.setExceptionMappings(mappings); // None by default
		r.setExceptionAttribute("ex"); // Default is "exception"
		r.setWarnLogCategory("demo1.ExceptionLogger"); // No default

		// Normally Spring MVC has no default error view and this class is the
		// only way to define one. A nice feature of Spring Boot is the ability
		// to provide a very basic default error view (otherwise the application
		// server typically returns a Java stack trace which is not acceptable
		// in production). By defining an @Bean method called defaultErrorView()
		// you can substitute your own error page.
		//
		// To stick with the Spring Boot approach, do not set this property of
		// SimpleMappingExceptionResolver and, optionally, supply your own
		// defaultErrorView() to setup an error view.
		//
		// Here we are choosing to use SimpleMappingExceptionResolver since many
		// Spring applications have used the approach since Spring V1. Therefore
		// the defaultErrorView() below returns null to disable Spring Boot's
		// error view.
		r.setDefaultErrorView("error");
		return r;
	}

	/**
	 * This method allows {@link SimpleMappingExceptionResolver} to work
	 * alongside Spring Boot by letting the resolver define the default
	 * error-view instead of Spring Boot. How? Because this method returns
	 * <tt>null</tt> deliberately.
	 * <p>
	 * <b>Background:</b> Spring Boot defines a default error view entitled
	 * "Whitelabel Error Page" - see {@link ErrorMvcAutoConfiguration} for more
	 * details. We want to define the default error view using a
	 * SimpleMappingExceptionResolver (as many Spring applications have done
	 * since Spring V1). By returning a null bean, this method stops Spring Boot
	 * defining any default error-view and allows SimpleMappingExceptionResolver
	 * to do it instead.
	 * 
	 * @return Always null - no default required, SimpleMappingExceptionResolver
	 *         will configure it instead.
	 */
	@Bean(name = "error")
	public View defaultErrorView() {
		logger.info("Invoked defaultErrorView - returning null");

		// Deliberately return no bean. Overrides Spring Boot's
		// ErrorMvcAutoConfiguration and lets SimpleMappingExceptionResolver
		// work as normal
		return null;
	}

}