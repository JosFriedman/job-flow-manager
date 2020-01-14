package gov.nyc.doitt.jobstatemanager.domain;

import org.springframework.http.HttpStatus;

public class JobStateManagerException extends RuntimeException {

	static final long serialVersionUID = -1L;

	public JobStateManagerException(String message) {
		super(message);
	}

	public JobStateManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobStateManagerException(Throwable cause) {
		super(cause);
	}

	protected HttpStatus getHttpStatus() {
		return HttpStatus.UNPROCESSABLE_ENTITY;
	}

}