package gov.nyc.doitt.jobstatusmanager.domain.jobstatus;

public class JobStatusManagerException extends RuntimeException {

	static final long serialVersionUID = -1L;

	public JobStatusManagerException() {
		super();
	}

	public JobStatusManagerException(String message) {
		super(message);
	}

	public JobStatusManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobStatusManagerException(Throwable cause) {
		super(cause);
	}
}