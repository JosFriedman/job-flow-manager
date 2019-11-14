package gov.nyc.doitt.jobstatusmanager.infrastructure;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.FieldError;


public class JobFlowManagerException extends RuntimeException {

	static final long serialVersionUID = -1L;

	public JobFlowManagerException() {
		super();
	}

	public JobFlowManagerException(String message) {
		super(message);
	}

	public JobFlowManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobFlowManagerException(Throwable cause) {
		super(cause);
	}

	public JobFlowManagerException(List<FieldError> fieldErrors) {
		this(fieldErrors.stream().map(p -> p.getField() + ": " + p.getCode()).collect(Collectors.joining(", ")));
	}

}