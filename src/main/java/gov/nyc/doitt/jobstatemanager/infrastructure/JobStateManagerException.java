package gov.nyc.doitt.jobstatemanager.infrastructure;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.FieldError;


public class JobStateManagerException extends RuntimeException {

	static final long serialVersionUID = -1L;

	public JobStateManagerException() {
		super();
	}

	public JobStateManagerException(String message) {
		super(message);
	}

	public JobStateManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobStateManagerException(Throwable cause) {
		super(cause);
	}

	public JobStateManagerException(List<FieldError> fieldErrors) {
		this(fieldErrors.stream().map(p -> p.getField() + ": " + p.getCode()).collect(Collectors.joining(", ")));
	}

}