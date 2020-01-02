package gov.nyc.doitt.jobstatemanager.infrastructure;

public class JobStateManagerConcurrencyException extends RuntimeException {

	static final long serialVersionUID = -1L;

	public JobStateManagerConcurrencyException() {
		super();
	}

	public JobStateManagerConcurrencyException(String message) {
		super(message);
	}

	public JobStateManagerConcurrencyException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobStateManagerConcurrencyException(Throwable cause) {
		super(cause);
	}
}