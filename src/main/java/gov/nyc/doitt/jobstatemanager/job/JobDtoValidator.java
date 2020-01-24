package gov.nyc.doitt.jobstatemanager.job;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;

/**
 * Validates the JobDto payload in REST calls
 */
@SuppressWarnings("unused")
@Component
class JobDtoValidator implements SmartValidator {

	@Override
	public boolean supports(Class<?> clazz) {
		return JobDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		validate(target, errors, (Object[]) null);
	}

	@Override
	public void validate(Object target, Errors errors, Object... validationHints) {

		JobDto jobDto = (JobDto) target;
		String prefix = getErrorMessagePrefix(validationHints);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "appName", prefix + "appName must be specified");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobId", prefix + "jobId must be specified");
	}

	private String getErrorMessagePrefix(Object[] validationHints) {

		return ArrayUtils.isEmpty(validationHints) || validationHints[0] == null ? "" : "jobDto[" + validationHints[0] + "].";

	}
}