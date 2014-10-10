package demo.config;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import demo.main.Main;
import demo.main.Profiles;

/**
 * Setup for exception handling using a {@link SimpleMappingExceptionResolver}
 * bean.
 * <p>
 * The use of the "demo-config" profile here is for demonstration only. A real
 * application would either create a {@link SimpleMappingExceptionResolver} or
 * it wouldn't.
 * 
 * @author Paul Chapman
 */
@Configuration
@Profile(Profiles.DEMO_CONFIG_PROFILE)
public class DemoExceptionConfiguration {

	protected Logger logger;

	public DemoExceptionConfiguration() {
		logger = LoggerFactory.getLogger(getClass());
		logger.info("Creating DemoExceptionConfiguration");
	}

	/**
	 * A sub-class of {@link SimpleMappingExceptionResolver} that can be turned
	 * on and off for demonstration purposes (you wouldn't do this in a real
	 * application).
	 */
	public class SwitchableSimpleMappingExceptionResolver extends
			SimpleMappingExceptionResolver {

		protected boolean enabled = false;

		public SwitchableSimpleMappingExceptionResolver(boolean enabled) {
			this.enabled = enabled;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		/**
		 * Resolver only handles exceptions if enabled. Overrides method
		 * inherited from {@link AbstractHandlerExceptionResolver}
		 */
		@Override
		protected boolean shouldApplyTo(HttpServletRequest request,
				Object handler) {
			return enabled && super.shouldApplyTo(request, handler);
		}

	}

	/**
	 * Setup the switchable SimpleMappingExceptionResolver to provide useful
	 * defaults for logging and handling exceptions.
	 * 
	 * @return The new resolver
	 */
	@Bean(name = "simpleMappingExceptionResolver")
	public SwitchableSimpleMappingExceptionResolver createSwitchableSimpleMappingExceptionResolver() {
		logger.info("Creating SwitchableSimpleMappingExceptionResolver in disabled mode");
		SwitchableSimpleMappingExceptionResolver r = new SwitchableSimpleMappingExceptionResolver(
				false);

		Properties mappings = new Properties();
		mappings.setProperty("DatabaseException", "databaseException");
		mappings.setProperty("InvalidCreditCardException", "creditCardError");

		r.setExceptionMappings(mappings); // None by default
		r.setExceptionAttribute("ex"); // Default is "exception"
		r.setWarnLogCategory("demo1.ExceptionLogger"); // No default

		// See comment in ExceptionConfiguration
		r.setDefaultErrorView("defaultErrorPage");
		return r;
	}

	/**
	 * A simple controller for enabling and disabling the
	 * {@link SwitchableSimpleMappingExceptionResolver}.
	 */
	@Controller
	@Profile("no-auto-creation")
	private static class SwitchController {

		protected SwitchableSimpleMappingExceptionResolver resolver;

		@Autowired
		public SwitchController(
				SwitchableSimpleMappingExceptionResolver resolver) {
			this.resolver = resolver;
		}

		/**
		 * Must return the currently active profiles for the home page
		 * (index.html) to use.
		 * 
		 * @return Currently active profiles.
		 */
		@ModelAttribute("profiles")
		public String getProfiles() {
			return Main.getProfiles();
		}

		/**
		 * Enable the profile if <code>action</code> is "on".
		 * 
		 * @param action
		 *            What to do. Resolver is enabled if set to "ON" (case
		 *            ignored).
		 * @return Redirection back to the home page.
		 */
		@RequestMapping("/simpleMappingExceptionResolver/{action}")
		public String enable(@PathVariable("action") String action, Model model) {
			LoggerFactory.getLogger(getClass()).info(
					"SwitchableSimpleMappingExceptionResolver is " + action);
			// Anything other than ON, is treated as OFF
			boolean enabled = action.equalsIgnoreCase("on");
			resolver.setEnabled(enabled);

			// The ResponseDataControllerAdvice sets this for every request, but
			// before it is changed by this controller. So we need to set it
			// again - to reflect the new value.
			model.addAttribute("switchState", enabled ? "on" : "off");
	
			return enabled ? "unannotated" : "no-handler";
		}
	}

	/**
	 * Create the {@link SwitchController}.
	 */
	@Bean(name = "switchController")
	public SwitchController createSwitchController() {
		logger.info("Creating SwitchController");
		return new SwitchController(
				createSwitchableSimpleMappingExceptionResolver());
	}

}
