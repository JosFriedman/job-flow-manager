package gov.nyc.doitt.jobstatemanager.security;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * Thrown for job authorization failures
 */
public class JobAuthorizationException extends OAuth2Exception {

	private static final long serialVersionUID = 1L;

	public JobAuthorizationException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobAuthorizationException(String message) {
		super(message);
	}

}
