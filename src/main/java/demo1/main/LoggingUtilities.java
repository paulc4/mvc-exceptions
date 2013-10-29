package demo1.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring3.SpringTemplateEngine;

import ch.qos.logback.classic.Level;

/**
 * Handy utility function for setting logging levels (slf4j loggers do not
 * directly allow this).
 * <p>
 * Nothing to do with exception handling as such, but I wanted to enable logging
 * in Spring MVC so we can see view resolution happening.
 * 
 * @author Paul Chapman
 */
@Component
@ConditionalOnClass(SpringTemplateEngine.class)
public class LoggingUtilities {

	static Logger logger = LoggerFactory.getLogger(LoggingUtilities.class);

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

		// Should be a Logback logger, cast down to set logging level
		if (slf4jLogger instanceof ch.qos.logback.classic.Logger)
			((ch.qos.logback.classic.Logger) slf4jLogger).setLevel(Level
					.toLevel(newLevel));
		else
			// Should never happen ...
			slf4jLogger.warn("Unrecognised logger "
					+ slf4jLogger.getClass().getSimpleName()
					+ " - unable to set logging level for " + loggerName);
	}
}
