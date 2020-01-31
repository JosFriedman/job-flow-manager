package gov.nyc.doitt.jobstatemanager.authorization;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * Thrown for user-validation failures
 */
public class UserValidationException extends OAuth2Exception {

	private static final long serialVersionUID = 1L;

	public UserValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserValidationException(String message) {
		super(message);
	}

}
