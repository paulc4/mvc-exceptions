package demo1.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.DefaultTemplateResolverConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring3.SpringTemplateEngine;

/**
 * A couple of handy utility functions. Nothing to do with exception handling,
 * but useful for setting up the framework.
 * 
 * @author Paul Chapman
 */
@Component
@ConditionalOnClass(SpringTemplateEngine.class)
public class ThymeleafHelper {

	static Logger logger = LoggerFactory.getLogger(ThymeleafHelper.class);

	@Autowired ResourceLoader resourceLoader;
	@Autowired Environment environment;

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
	public Boolean thymeleafViewExists(String viewName) {
		return DefaultTemplateResolverConfiguration.templateExists(
				environment, resourceLoader, viewName);
	}

}
