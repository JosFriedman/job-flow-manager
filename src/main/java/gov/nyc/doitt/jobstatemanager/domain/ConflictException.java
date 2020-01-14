package gov.nyc.doitt.jobstatemanager.domain;

import org.springframework.http.HttpStatus;

public class ConflictException extends JobStateManagerException {

	static final long serialVersionUID = -1L;

	public ConflictException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConflictException(String message) {
		super(message);
	}

	public ConflictException(Throwable cause) {
		super(cause);
	}

	@Override
	protected HttpStatus getHttpStatus() {
		return HttpStatus.CONFLICT;
	}

}