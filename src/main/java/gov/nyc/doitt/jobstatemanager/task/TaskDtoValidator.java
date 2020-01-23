package gov.nyc.doitt.jobstatemanager.task;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;

/**
 * Validates the TaskDto payload in REST calls
 */
@SuppressWarnings("unused")
@Component
class TaskDtoValidator implements SmartValidator {

	@Override
	public boolean supports(Class<?> clazz) {
		return TaskDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		validate(target, errors, (Object[]) null);
	}

	@Override
	public void validate(Object target, Errors errors, Object... validationHints) {

		TaskDto taskDto = (TaskDto) target;
		String prefix = getErrorMessagePrefix(validationHints);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobId", prefix + "jobId must be specified");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state", prefix + "state must be specified");
	}

	private String getErrorMessagePrefix(Object[] validationHints) {

		return ArrayUtils.isEmpty(validationHints) || validationHints[0] == null ? "" : "taskDto[" + validationHints[0] + "].";

	}
}