package demo1.main;

import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import ch.qos.logback.classic.Level;

/**
 * A couple of handy utility functions. Nothing to do with exception handling,
 * but useful for setting up the framework.
 * 
 * @author Paul Chapman
 */
public class DemoUtilities {

	static Logger logger = LoggerFactory.getLogger(DemoUtilities.class);

	/**
	 * Set the logging level of the specified logger.
	 * 
	 * @param loggerName
	 *            Name of a logger - typically a package name or a fully
	 *            qualified class name.
	 * @param newLevel
	 *            The new logging level. Accepts logback/log4j strings: All,
	 *            DEBUG, ERROR, INFO, OFF, TRACE, WARN
	 */
	public static void setLogLevel(String loggerName, String newLevel) {
		Logger slf4jLogger = LoggerFactory.getLogger(loggerName);

		// Work out what type of logging sl4j is using and set the logging level
		if (slf4jLogger instanceof ch.qos.logback.classic.Logger)
			((ch.qos.logback.classic.Logger) slf4jLogger).setLevel(Level
					.toLevel(newLevel));
		else
			// Add code for your favorite logging library here ...
			;
	}

	/**
	 * Check to see if the specified view genuinely exists as a Thymeleaf
	 * template.
	 * 
	 * @param viewName
	 *            Name of a view (logical view name returned from a @Controller
	 *            request-handling method).
	 * @param prefix
	 *            The location of Thymeleaf templates.
	 * @param suffix
	 *            The file-type of Thymeleaf templates.
	 * @return Boolean TRUE if found, FALSE if not or null if existence could
	 *         not be determined.
	 */
	public static Boolean thymeleafViewExists(String viewName, String prefix,
			String suffix) {
		String viewPath = prefix + viewName + suffix;
		logger.info("Checking view: " + viewName + " looking for " + viewPath);

		Resource res = null;

		if (viewPath.startsWith("classpath:"))
			res = new ClassPathResource(viewPath.substring(10));
		else if (viewPath.startsWith("file:"))
			res = new FileSystemResource(viewPath.substring(5));
		else {
			try {
				res = new UrlResource(viewPath);
			} catch (MalformedURLException e) {
				logger.info("Unrecognised resource " + viewName);
				return null;   // Can't decide, give up
			}
		}

		if (!res.exists()) {
			logger.info(viewPath + " not found, skipping Thymeleaf");
			return Boolean.FALSE;
		}

		logger.info("Found " + viewName + ", using Thymeleaf");
		return Boolean.TRUE;
	}
}
