package gov.nyc.doitt.jobflowmanager.infrastructure;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.FieldError;


public class AAAAEntityNotFoundException extends RuntimeException {

	static final long serialVersionUID = -1L;

	public AAAAEntityNotFoundException() {
		super();
	}

	public AAAAEntityNotFoundException(String message) {
		super(message);
	}

	public AAAAEntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public AAAAEntityNotFoundException(Throwable cause) {
		super(cause);
	}

	public AAAAEntityNotFoundException(List<FieldError> fieldErrors) {
		this(fieldErrors.stream().map(p -> p.getField() + ": " + p.getCode()).collect(Collectors.joining(", ")));
	}

}