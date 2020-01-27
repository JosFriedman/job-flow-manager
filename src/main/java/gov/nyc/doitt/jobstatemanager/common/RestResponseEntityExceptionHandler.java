package gov.nyc.doitt.jobstatemanager.common;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

	@ExceptionHandler(value = { JobStateManagerException.class })
	protected ResponseEntity<Object> handleJobStateManagerException(JobStateManagerException ex, WebRequest request) {

		Map<String, Object> body = new LinkedHashMap<>();
		List<String> errors = ex.getErrors();
		if (errors != null) {
			body.put("errors", errors);
		} else {
			body.put("error", ex.getMessage());			
		}
		logger.error(ex.getMessage(), ex);
		return handleExceptionInternal(ex, body, new HttpHeaders(), ex.getHttpStatus(), request);
	}

	// error handler for @Valid
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		logger.error(ex.getMessage(), ex);

		Map<String, Object> body = new LinkedHashMap<>();
		// Get all errors
		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(p -> p.getDefaultMessage())
				.collect(Collectors.toList());

		body.put("errors", errors);
		return handleExceptionInternal(ex, body, headers, status, request);

	}

}