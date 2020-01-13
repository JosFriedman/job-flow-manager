package gov.nyc.doitt.jobstatemanager.infrastructure;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends JobStateManagerException {

	static final long serialVersionUID = -1L;

	public EntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntityNotFoundException(String message) {
		super(message);
	}

	public EntityNotFoundException(Throwable cause) {
		super(cause);
	}

	@Override
	protected HttpStatus getHttpStatus() {
		return HttpStatus.NOT_FOUND;
	}

}