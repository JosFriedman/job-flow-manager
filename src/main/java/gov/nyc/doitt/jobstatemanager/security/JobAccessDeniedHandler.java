package gov.nyc.doitt.jobstatemanager.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Handler for AccessDeniedExceptions
 */
@Component
public class JobAccessDeniedHandler extends AbstractExceptionHandler implements AccessDeniedHandler {

	@Override
	/**
	 * Return exception in standard Content API error JSON
	 */
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
			throws IOException, ServletException {

		doHandle(request, response, ex);

	}

}
