package gov.nyc.doitt.jobstatusmanager.infrastructure;

public class JobFlowManagerConcurrencyException extends RuntimeException {

	static final long serialVersionUID = -1L;

	public JobFlowManagerConcurrencyException() {
		super();
	}

	public JobFlowManagerConcurrencyException(String message) {
		super(message);
	}

	public JobFlowManagerConcurrencyException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobFlowManagerConcurrencyException(Throwable cause) {
		super(cause);
	}
}