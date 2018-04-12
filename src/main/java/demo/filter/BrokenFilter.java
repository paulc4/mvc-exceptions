package demo.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;

/**
 * A demo filter that deliberately throws an exception if the URL "/broken" is
 * invoked. Due to Spring Boot auto-configuration, the exception will be
 * reported via the usual Spring Boot page (by default the Whitelabel error page
 * but in this application, our Thymeleaf error-page).
 * <p>
 * As this is a Spring Boot application, the filter can be created as a Spring
 * Bean by the component scanner in the usual way and Spring Boot will register
 * it as a filter, mapped to "/*" by default. For more control consider creating
 * a {@link FilterRegistrationBean} to configure it.
 */
@Component
public class BrokenFilter implements Filter {

	@SuppressWarnings("serial")
	protected static class FilterException extends RuntimeException {

		public FilterException(String message) {
			super(message);
		}
	}

	private Logger logger = Logger.getLogger(BrokenFilter.class.getName());

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (request instanceof HttpServletRequest) {
			if (((HttpServletRequest) request).getRequestURI().endsWith("broken")) {
				logger.severe("BROKEN FILTER FORCES AN EXCEPTION");
				throw new FilterException("Failure in BrokenFilter");
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
