package gov.nyc.doitt.jobstatusmanager.domain;

public class JobStatusManagerConcurrencyException extends RuntimeException {

	static final long serialVersionUID = -1L;

	public JobStatusManagerConcurrencyException() {
		super();
	}

	public JobStatusManagerConcurrencyException(String message) {
		super(message);
	}

	public JobStatusManagerConcurrencyException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobStatusManagerConcurrencyException(Throwable cause) {
		super(cause);
	}
}