package gov.nyc.doitt.jobstatemanager.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.error.OAuth2ExceptionRenderer;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Base handler for Security Exceptions
 */
abstract public class AbstractExceptionHandler {

	private static final Log log = LogFactory.getLog(AbstractExceptionHandler.class);

	public static final String ACCESS_DENIED_ERROR_CODE = "accessDenied";
	public static final String AUTHENTICATION_ERROR_CODE = "authenticationError";

	@Autowired
	private HandlerExceptionResolver handlerExceptionResolver;

	@Autowired
	private OAuth2ExceptionRenderer exceptionRenderer;

	/**
	 * Return AccessDeniedException in our standard error JSON
	 */
	protected void doHandle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
			throws IOException, ServletException {

		// log at different levels so stack trace can be filtered out
		log.fatal("AccessDeniedException: " + ex.getMessage());
		log.error("AccessDeniedException: ", ex);

		ResponseEntity<ErrorResponse> result = new ResponseEntity<>(new ErrorResponse(ACCESS_DENIED_ERROR_CODE, ex.getMessage()),
				HttpStatus.FORBIDDEN);
		render(request, response, result);
	}

	/**
	 * Return AuthenticationException in our standard error JSON
	 */
	protected void doHandle(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
			throws IOException, ServletException {

		// log at different levels so stack trace can be filtered out
		log.fatal("AuthenticationException: " + ex.getMessage());
		log.error("AuthenticationException: ", ex);

		ResponseEntity<ErrorResponse> result = new ResponseEntity<>(new ErrorResponse(AUTHENTICATION_ERROR_CODE, ex.getMessage()),
				HttpStatus.UNAUTHORIZED);
		render(request, response, result);
	}

	/**
	 * Render exception in standard Content API error JSON
	 */
	private void render(HttpServletRequest request, HttpServletResponse response, ResponseEntity<ErrorResponse> result)
			throws IOException, ServletException {

		try {
			exceptionRenderer.handleHttpEntityResponse(result, new ServletWebRequest(request, response));
			response.flushBuffer();

		} catch (ServletException e) {
			// Re-use some of the default Spring dispatcher behaviour - the exception came from the filter chain and
			// not from an MVC handler so it won't be caught by the dispatcher (even if there is one)
			if (handlerExceptionResolver.resolveException(request, response, this, e) == null) {
				throw e;
			}
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

}
