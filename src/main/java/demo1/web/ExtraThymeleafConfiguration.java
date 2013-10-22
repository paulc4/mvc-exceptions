package demo1.web;

import java.util.Locale;

import javax.servlet.Servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;

import demo1.main.DemoUtilities;
import demo1.main.Main;

/**
 * Overrides the default Thymeleaf configuration in Spring Boot's
 * {@link ThymeleafAutoConfiguration} to setup a customised Thymeleaf
 * view-resolver that can detect which views it should process and which it
 * should not. Allows Thymeleaf and JSP to be used in the same application.
 * <p>
 * By default the {@link ThymeleafViewResolver} always returns a view regardless
 * of whether it exists or not. This can be controlled by using its
 * <tt>setViewNames</tt> and <tt>setExcludedViewNames</tt> properties to
 * explicitly itemise which view names it does/does not support - see
 * <tt>ThymeleafViewResolver.canHandle()</tt> to see how this works.
 * <p>
 * Unfortunately, this is not very scalable and easily fails if these lists are
 * not updated for new views. Also the default Thymeleaf setup from Spring Boot
 * cannot enable this for you (it would need to know about your application
 * view-names to do so).
 * <p>
 * There are two possible solutions.
 * <p>
 * <b>Option 1:</b> Provide a <tt>@Bean</tt> method called
 * <tt>thymeleafViewResolver()</tt> to override what Spring Boot does
 * automatically. In here you can invoke <tt>setViewNames</tt> and
 * <tt>setExcludedViewNames</tt> to explicity list which view-names are/are not
 * managed by Thymeleaf. All excluded view-names fall back on JSP.
 * <p>
 * <b>Option 2:</b> Extend {@link ThymeleafViewResolver} to explicitly check if
 * a template exists or not. The code to do this can be found in
 * {@link DemoUtilities#thymeleafViewExists(String, String, String)}. The
 * <tt>CustomThymeleafViewResolver</tt> overrides the <tt>canHandle</tt> method
 * to perform an explicit search.
 * <p>
 * <b>Note on JSP:</b> To configure JSP's <tt>InternalResourceViewResolver</tt>
 * you need to specify configuration properties <tt>spring.view.prefix</tt> and
 * <tt>spring.view.prefix</tt> so that Spring Boot will pick them up - see
 * {@link Main}.
 * 
 * @author Paul Chapman
 */
@Configuration
@ConditionalOnClass({ Servlet.class })
public class ExtraThymeleafConfiguration {

	/**
	 * Get Spring to auto-inject the templateEngine used by Thymeleaf. This is
	 * created by {@link ThymeleafAutoConfiguration}.
	 */
	@Autowired
	protected SpringTemplateEngine templateEngine;

	/**
	 * Specifies the location of Thymeleaf templates. Uses the same default as
	 * Spring Boot: <tt>classpath:/templates</tt>. Unfortunately this has to be
	 * hard-coded.
	 */
	@Value("${spring.thymeleaf.prefix:classpath:/templates/}")
	protected String thymeleafPrefix = "";

	/**
	 * Specifies the file-type of Thymeleaf templates. Uses the same default as
	 * Spring Boot: <tt>html</tt>. Unfortunately this has to be hard-coded.
	 */
	@Value("${spring.thymeleaf.suffix:.html}")
	protected String thymeleafSuffix = "";

	/**
	 * Get Thymeleaf to actually check for its templates. Choices are:
	 * <ul>
	 * <li><tt>false</tt> Option 1 - define views to generate/ignore explicitly</li>
	 * <li><tt>true&nbsp;</tt> Option 2 - use the
	 * {@link CustomThymeleafViewResolver}</li>
	 * </ul>
	 */
	protected boolean useSmartViewResolver = false;

	/**
	 * Internal logger.
	 */
	protected Logger logger;

	public ExtraThymeleafConfiguration() {
		logger = LoggerFactory.getLogger(getClass());

		// Enable internal logging for Spring MVC
		DemoUtilities.setLogLevel("org.springframework.web", "DEBUG");
	}

	/**
	 * A subclass of {@link ThymeleafViewResolver} that explicitly tries to
	 * check if a requested template exists. Uses Spring's Resource classes to
	 * looks for requested view names. Corresponds to Option 2 above.
	 * <p>
	 * See {@link #canHandle(String, Locale)} for more details.
	 * 
	 * @author Paul Chapman
	 */
	public class CustomThymeleafViewResolver extends ThymeleafViewResolver {

		public String templateDir; // Prefix
		public String fileType; // Suffix

		/**
		 * Stash away the suffix (template directory) and suffix (file-type).
		 * This data is also in the Template Engine but is not available when
		 * this method is called (the engine is not initialised).
		 * 
		 * @param templateDir
		 * @param fileType
		 */
		public CustomThymeleafViewResolver(String templateDir, String fileType) {
			this.fileType = fileType;
			this.templateDir = templateDir;
		}

		/**
		 * The default implementation simply checks the <tt>viewNames</tt> and
		 * <tt>excludedViewNames</tt> lists. This is why Spring-Boot cannot do
		 * this for you out-of-the-box.
		 * <p>
		 * Instead this code actually looks for the resource to see if it
		 * exists. This view resolver caches its views after the first time, so
		 * the overhead of this check may not be that bad.
		 */
		@Override
		protected boolean canHandle(final String viewName, final Locale locale) {
			// To keep this method simple, the logic is in DemoUtilities
			Boolean exists = DemoUtilities.thymeleafViewExists(viewName,
					templateDir, fileType);
			return (exists == null) ? super.canHandle(viewName, locale)
					: exists;
		}
	}

	/**
	 * Overrides the default Thymeleaf setup from Spring Boot (for details see
	 * {@link ThymeleafAutoConfiguration}) to allow Thymeleaf resovler to work
	 * alongside JSP.
	 * 
	 * @return A customised/configured resolver.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "thymeleafViewResolver")
	public ThymeleafViewResolver thymeleafViewResolver() {

		// customised version that checks if templates exist
		ThymeleafViewResolver resolver;

		if (!useSmartViewResolver) {
			// Option 1: Use the default ThymeleafViewResolver, explicitly
			// exempt the view named "support". It will be generated by JSP
			// instead.
			resolver = new ThymeleafViewResolver();
			resolver.setExcludedViewNames(new String[] { "support" });
		} else {
			// Option 2: Use the customised resolver that actually checks each
			// view to see if there is a corresponding template. Thymeleaf's
			// view-resolver does not do this for performance reasons. Your
			// choice.
			resolver = new CustomThymeleafViewResolver(thymeleafPrefix,
					thymeleafSuffix);
		}

		// The rest of this method is simply copied from
		// ThymeleafAutoConfiguration
		resolver.setTemplateEngine(this.templateEngine);
		resolver.setCharacterEncoding("UTF-8");

		// Needs to come before any fallback resolver (in our case the
		// InternalResourceViewResolver for JSP)
		resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 20);

		return resolver;
	}

}
