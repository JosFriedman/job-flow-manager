package gov.nyc.doitt.jobstatemanager.common;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

public class JobStateManagerException extends RuntimeException {

	private static final long serialVersionUID = -1L;

	private List<String> errors;

	public JobStateManagerException(List<String> errors) {
		this(errors.stream().collect(Collectors.joining(", ")));
		this.errors = errors;
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

	protected HttpStatus getHttpStatus() {
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

	public List<String> getErrors() {
		return errors;
	}

}