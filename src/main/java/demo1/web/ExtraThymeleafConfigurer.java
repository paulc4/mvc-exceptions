package demo1.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;

/**
 * Enable Thymeleaf and JSP to be used in the same application.
 * <p>
 * By default the {@link ThymeleafViewResolver} always returns a view regardless
 * of whether it exists or not. This can be controlled by using its
 * <tt>setViewNames</tt> and <tt>setExcludedViewNames</tt> properties to
 * explicitly itemise which view names it does/does not support - see
 * <tt>ThymeleafViewResolver.canHandle()</tt> to see how this works.
 * <p>
 * Here we simply pull in the <tt>ThymeleafViewResolver</tt> and configure it to
 * ignore the "support" view which we want JSP to render instead.
 * 
 * @author Paul Chapman
 */
@Component
@ConfigurationProperties("spring.view")
@ConditionalOnClass({ ThymeleafViewResolver.class })
public class ExtraThymeleafConfigurer {

	/**
	 * Internal logger.
	 */
	protected Logger logger;

	@Autowired
	public ExtraThymeleafConfigurer(ThymeleafViewResolver resolver) {
		logger = LoggerFactory.getLogger(getClass());

		// Hide support from Thymeleaf so it can be rendered via JSP instead.
		logger.info("Hiding 'support' view from Thymeleaf.  Allows JSP to render it instead.");
		resolver.setExcludedViewNames(new String[] { "support" });
	}
}
