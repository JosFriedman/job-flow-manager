package gov.nyc.doitt.jobstatemanager.infrastructure;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

public class ValidationException extends JobStateManagerException {

	static final long serialVersionUID = -1L;

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(Throwable cause) {
		super(cause);
	}

	public ValidationException(List<FieldError> fieldErrors) {
		this(fieldErrors.stream().map(p -> p.getField() + ": " + p.getCode()).collect(Collectors.joining(", ")));
	}


	@Override
	protected HttpStatus getHttpStatus() {
		return HttpStatus.UNPROCESSABLE_ENTITY;
	}


}