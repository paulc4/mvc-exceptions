package demo1.web;

import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import demo1.exceptions.CustomException;
import demo1.exceptions.DatabaseException;
import demo1.exceptions.InvalidCreditCardException;
import demo1.exceptions.OrderNotFoundException;
import demo1.exceptions.UnhandledException;
import demo1.main.Profiles;

/**
 * A controller whose request-handler methods deliberately throw exceptions to
 * demonstrate the points discussed in the Blog.
 * <p>
 * This controller is only created when the "controller" profile is active. It
 * contains <tt>@ExceptionHandler</tt> methods to handle (most of) the
 * exceptions it raises.
 * 
 * @author Paul Chapman
 */
@Controller
@Profile(Profiles.CONTROLLER_PROFILE)
public class ExceptionHandlingController {

	@Autowired
	ThymeleafHelper helper;

	protected Logger logger;

	public ExceptionHandlingController() {
		logger = LoggerFactory.getLogger(getClass());
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* . . . . . . . . . . . . . . MODEL ATTRIBUTES . . . . . . . . . . . . .. */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

	/**
	 * What profile are we currently using?
	 * <p>
	 * Note that error views do not have automatically have access to the model,
	 * so they do not have access to model-attributes either.
	 * 
	 * @return Always "CONTROLLER".
	 */
	@ModelAttribute("profile")
	public String getProfile() {
		return Profiles.CONTROLLER_PROFILE.toUpperCase();
	}

	/**
	 * Required for compatibility with Spring Boot.
	 * 
	 * @return Date and time of current request.
	 */
	@ModelAttribute("timestamp")
	public String getTimestamp() {
		return new Date().toString();
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* . . . . . . . . . . . . . . REQUEST HANDLERS . . . . . . . . . . . . .. */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

	/**
	 * Controller to demonstrate exception handling..
	 * 
	 * @return The view name (an HTML page with Thymeleaf markup).
	 */
	@RequestMapping("/")
	String home(Model model) {
		logger.info("Home page");
		return "index";
	}

	/**
	 * No handler is needed for this exception since it is annotated with
	 * <tt>@ResponseStatus</tt>.
	 * 
	 * @return Nothing - it always throws the exception.
	 * @throws OrderNotFoundException
	 *             Always thrown.
	 */
	@RequestMapping("/orderNotFound")
	String throwOrderNotFoundException() {
		logger.info("Throw OrderNotFoundException for unknown order 12345");
		throw new OrderNotFoundException("12345");
	}

	/**
	 * Throws an unannotated <tt>DataIntegrityViolationException</tt>. Must be
	 * caught by an exception handler.
	 * 
	 * @return Nothing - it always throws the exception.
	 * @throws DataIntegrityViolationException
	 *             Always thrown.
	 */
	@RequestMapping("/dataIntegrityViolation")
	String throwDataIntegrityViolationException() throws SQLException {
		logger.info("Throw DataIntegrityViolationException");
		throw new DataIntegrityViolationException("Duplicate id");
	}

	/**
	 * Simulates a database exception by always throwing <tt>SQLException</tt>.
	 * Must be caught by an exception handler.
	 * 
	 * @return Nothing - it always throws the exception.
	 * @throws SQLException
	 *             Always thrown.
	 */
	@RequestMapping("/databaseError1")
	String throwDatabaseException1() throws SQLException {
		logger.info("Throw SQLException");
		throw new SQLException();
	}

	/**
	 * Simulates a database exception by always throwing
	 * <tt>DataAccessException</tt>. Must be caught by an exception handler.
	 * 
	 * @return Nothing - it always throws the exception.
	 * @throws DataAccessException
	 *             Always thrown.
	 */
	@RequestMapping("/databaseError2")
	String throwDatabaseException2() throws DataAccessException {
		logger.info("Throw DataAccessException");
		throw new DataAccessException("Error accessing database");
	}

	/**
	 * Simulates an illegal credit-card exception by always throwing
	 * <tt>InvalidCreditCardException</tt>. Handled by
	 * <tt>SimpleMappingExceptionResolver</tt>.
	 * 
	 * @return Nothing - it always throws the exception.
	 * @throws InvalidCreditCardException
	 *             Always thrown.
	 */
	@RequestMapping("/invalidCreditCard")
	String throwInvalidCreditCard() throws Exception {
		logger.info("Throw InvalidCreditCardException");
		throw new InvalidCreditCardException("1234123412341234");
	}

	/**
	 * Simulates a database exception by always throwing
	 * <tt>DatabaseException</tt>. Handled by
	 * <tt>SimpleMappingExceptionResolver</tt>.
	 * 
	 * @return Nothing - it always throws the exception.
	 * @throws DatabaseException
	 *             Always thrown.
	 */
	@RequestMapping("/databaseException")
	String throwDatabaseException() throws Exception {
		logger.info("Throw InvalidCreditCardException");
		throw new DatabaseException("Database not found: info.db");
	}

	/**
	 * Simulates a database exception by always throwing
	 * <tt>CustomException</tt>. Must be caught by an exception handler.
	 * 
	 * @return Nothing - it always throws the exception.
	 * @throws CustomException
	 *             Always thrown.
	 */
	@RequestMapping("/customException")
	String throwCustomException() throws Exception {
		logger.info("Throw CustomException");
		throw new CustomException("Custom exception occurred");
	}

	/**
	 * Simulates a database exception by always throwing
	 * <tt>UnhandledException</tt>. Must be caught by an exception handler.
	 * 
	 * @return Nothing - it always throws the exception.
	 * @throws UnhandledException
	 *             Always thrown.
	 */
	@RequestMapping("/unhandledException")
	String throwUnhandledException() throws Exception {
		logger.info("Throw UnhandledException");
		throw new UnhandledException("Some exception occurred");
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* . . . . . . . . . . . . . EXCEPTION HANDLERS . . . . . . . . . . . . .. */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

	// Convert a predefined exception to an HTTP Status code
	@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Data integrity violation")
	// 409
	@ExceptionHandler(DataIntegrityViolationException.class)
	public void conflict() {
		logger.error("Request raised a DataIntegrityViolationException");
		// Nothing to do
	}

	// Specify the name of a specific view that will be used to display the
	// error:
	@ExceptionHandler({ SQLException.class, DataAccessException.class })
	public String databaseError(Exception exception) {
		// Nothing to do. Return value 'databaseError' used as logical view name
		// of an error page, passed to view-resolver(s) in usual way.
		logger.error("Request raised " + exception.getClass().getSimpleName());
		return "databaseError";
	}

	// Total control - setup a model and return the view name
	@ExceptionHandler(CustomException.class)
	public ModelAndView handleError(HttpServletRequest req, Exception exception)
			throws Exception {

		// Rethrow annotated exceptions or they will be processed here instead.
		if (AnnotationUtils.findAnnotation(exception.getClass(),
				ResponseStatus.class) != null)
			throw exception;

		logger.error("Request: " + req.getRequestURI() + " raised " + exception);

		ModelAndView mav = new ModelAndView();
		mav.addObject("exception", exception);
		mav.addObject("url", req.getRequestURI());
		mav.addObject("timestamp", getTimestamp());

		mav.setViewName("support");
		return mav;
	}
}