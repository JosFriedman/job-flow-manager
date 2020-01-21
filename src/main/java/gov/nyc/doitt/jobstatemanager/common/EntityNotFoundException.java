package gov.nyc.doitt.jobstatemanager.common;

import java.util.List;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends JobStateManagerException {

	private static final long serialVersionUID = 1L;

	public EntityNotFoundException(List<String> errors) {
		super(errors);
	}

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