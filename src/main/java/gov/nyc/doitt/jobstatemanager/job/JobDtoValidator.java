package gov.nyc.doitt.jobstatemanager.job;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates the JobDto payload in REST calls
 */
@Component
class JobDtoValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return JobDto.class.isAssignableFrom(clazz) /*|| List.class.isAssignableFrom(clazz) */;
	}

	@Override
	public void validate(Object target, Errors errors) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "appId", "appId must be specified");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobId", "jobId must be specified");
	}

}