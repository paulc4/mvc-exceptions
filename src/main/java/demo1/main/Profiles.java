package demo1.main;

import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import demo1.web.ControllerWithoutExceptionHandlers;
import demo1.web.ExceptionConfiguration;
import demo1.web.ExceptionHandlingController;
import demo1.web.GlobalControllerExceptionHandler;

/**
 * Spring Bean configuration profiles used in this application. Three profile
 * combinations are currently in use:
 * <ul>
 * <li><tt>CONTROLLER_PROFILE</tt> - creates a controller that also does its own
 * exception handling - see {@link ExceptionHandlingController}.
 * <li><tt>GLOBAL_PROFILE</tt> and <tt>JAVA_CONFIG_PROFILE</tt> - creates a
 * controller with no exception handlers. Instead exceptions are handled
 * globally - see {@link ControllerWithoutExceptionHandlers} and
 * {@link GlobalControllerExceptionHandler}. A
 * {@link SimpleMappingExceptionResolver} is also created using Java Config -
 * see {@link ExceptionConfiguration}.
 * <li><tt>GLOBAL_PROFILE</tt> and <tt>XML_CONFIG_PROFILE</tt> - as previous but
 * the {@link SimpleMappingExceptionResolver} is configured using XML - see
 * <tt>mvc-configuration.xml</tt>
 * </ul>
 * The ability to switch between Java or XML configuration for the
 * <tt>SimpleMappingExceptionResolver</tt> is just for demonstration purposes.
 * In a real application use one or the other, but not both. You are very likely
 * to have an existing XML definition for this bean, if you have an existing
 * application, and there is no need to change it.
 *
 * @author Paul Chapman
 */
public interface Profiles {

	/**
	 * Controller profile - exceptions handled by methods on the
	 * ExceptionController class. Value = <b>{@value} </b>
	 */
	public static final String CONTROLLER_PROFILE = "controller";
	/**
	 * Global profile - exceptions handled via a <tt>@ControllerAdvice</tt>
	 * instance. Value = <b>{@value} </b>
	 */
	public static final String GLOBAL_PROFILE = "global";
	/**
	 * Java configuration profile - see {@link ExceptionConfiguration}. Value =
	 * <b>{@value} </b>
	 */
	public static final String JAVA_CONFIG_PROFILE = "java-config";
	/**
	 * XML configuration property - see <tt>mvc-configuration.xml</tt>. Value =
	 * <b>{@value} </b>
	 */
	public static final String XML_CONFIG_PROFILE = "xml-config";

}