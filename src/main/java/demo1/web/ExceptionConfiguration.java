package demo1.web;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import demo1.main.Main;
import demo1.main.Profiles;

/**
 * Setup for exception handling using a {@link SimpleMappingExceptionResolver}
 * bean.
 * <p>
 * If you prefer to do this in XML set the {@link Main#useJavaConfig} property
 * to <tt>false</tt>
 * <p>
 * The use of the JAVA_CONFIG profile here is for demonstration only. A real
 * application wouldn't normally need to switch between XML or Java
 * Configuration. You would use one, or other, or both, all the time.
 * 
 * @author Paul Chapman
 */
@Configuration
@Profile(Profiles.JAVA_CONFIG_PROFILE)
public class ExceptionConfiguration {

	protected Logger logger;

	public ExceptionConfiguration() {
		logger = LoggerFactory.getLogger(getClass());
		logger.info("Creating ExceptionConfiguration");
	}

	/**
	 * Setup the classic SimpleMappingExceptionResolver. This provides useful
	 * defaults for logging and handling exceptions. It has been part of Spring
	 * MVC since Spring V2 and you will probably find most existing Spring MVC
	 * applications are using it.
	 * <p>
	 * Only invoked if the "global" profile is active.
	 * 
	 * @return The new resolver
	 */
	@Bean(name = "simpleMappingExceptionResolver")
	@Profile(Profiles.GLOBAL_PROFILE)
	public SimpleMappingExceptionResolver createSimpleMappingExceptionResolver() {
		logger.info("Creating SimpleMappingExceptionResolver");
		SimpleMappingExceptionResolver r = new SimpleMappingExceptionResolver();

		Properties mappings = new Properties();
		mappings.setProperty("DatabaseException", "databaseError");
		mappings.setProperty("InvalidCreditCardException", "creditCardError");

		r.setExceptionMappings(mappings); // None by default
		r.setExceptionAttribute("ex"); // Default is "exception"
		r.setWarnLogCategory("demo1.ExceptionLogger"); // No default

		/*
		 * Normally Spring MVC has no default error view and this class is the
		 * only way to define one. A nice feature of Spring Boot is the ability
		 * to provide a very basic default error view (otherwise the application
		 * server typically returns a Java stack trace which is not acceptable
		 * in production). Spring Boot tries to find a view called "error" that
		 * matches your view technology. Otherwise it defines a fall-back error
		 * page with the distinctive title of "Whitelabel Error Page". See
		 * ErrorMvcAutoConfiguration for more information.
		 * 
		 * By defining an @Bean method called defaultErrorView() you can
		 * substitute your own error page.
		 * 
		 * To stick with the Spring Boot approach, DO NOT set this property of
		 * SimpleMappingExceptionResolver.
		 * 
		 * Here we are choosing to use SimpleMappingExceptionResolver since many
		 * Spring applications have used the approach since Spring V1. Because
		 * we have called this view "error" it overrides Spring Boot's default.
		 */
		r.setDefaultErrorView("error");
		return r;
	}
}