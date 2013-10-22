package demo1.web;

import java.sql.SQLException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import demo1.exceptions.CustomException;
import demo1.exceptions.DatabaseException;
import demo1.exceptions.InvalidCreditCardException;
import demo1.exceptions.OrderNotFoundException;
import demo1.exceptions.UnhandledException;

/**
 * A controller whose request-handler methods deliberately throw exceptions to
 * demonstrate the points discussed in the Blog.
 * <p>
 * This controller is only created when the "global" profile is active. It
 * expects a <tt>@ControllerAdvice</tt> to handle its exceptions - see
 * {@link GlobalControllerExceptionHandler}.
 * 
 * @author Paul Chapman
 */
@Controller
@Profile("global")
public class ControllerWithoutExceptionHandlers {

	protected Logger logger;

	public ControllerWithoutExceptionHandlers() {
		logger = LoggerFactory.getLogger(getClass());
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* . . . . . . . . . . . . . . MODEL ATTRIBUTES . . . . . . . . . . . . . . */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

	/**
	 * What profile are we currently using?
	 * 
	 * @return Always "GLOBAL".
	 */
	@ModelAttribute("profile")
	public String getProfile() {
		return "GLOBAL";
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
	/* . . . . . . . . . . . . . . REQUEST HANDLERS . . . . . . . . . . . . . . */
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

}
