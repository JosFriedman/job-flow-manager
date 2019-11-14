package gov.nyc.doitt.jobstatusmanager.domain.jobstatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.validation.FieldError;


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

	public JobStatusManagerException(List<FieldError> fieldErrors) {
		this(fieldErrors.stream().map(p -> p.getField() + ": " + p.getCode()).collect(Collectors.joining(", ")));
	}

}