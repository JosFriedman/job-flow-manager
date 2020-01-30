package gov.nyc.doitt.jobstatemanager.jobconfig;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates the JobConfigDto payload in REST calls
 */
@SuppressWarnings("unused")
@Component
class JobConfigDtoValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return JobConfigDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		JobConfigDto jobConfigDto = (JobConfigDto) target;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobName", "jobName must be specified");

		List<TaskConfigDto> taskConfigDtos = jobConfigDto.getTaskConfigDtos();
		ValidationUtils.rejectIfEmpty(errors, "taskConfigDtos", "taskConfigDtos must be specified");
		if (taskConfigDtos == null) {
			return;
		}
		for (int i = 0; i < taskConfigDtos.size(); i++) {
			TaskConfigDto taskConfigDto = taskConfigDtos.get(i);
			String prefix = getPrefix(i);
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, prefix + "name", "name must be specified");
			if (taskConfigDto.getMaxBatchSize() < 1 || taskConfigDto.getMaxBatchSize() > 99) {
				errors.rejectValue(prefix + "maxBatchSize", "maxBatchSize must be >= 1 and <= 99");
			}
			if (taskConfigDto.getMaxRetriesForError() < 0 || taskConfigDto.getMaxRetriesForError() > 99) {
				errors.rejectValue(prefix + "maxRetriesForError", "maxRetriesForError must be >= 0 and <= 99");
			}
		}
	}

	private String getPrefix(int i) {
		return "taskConfigDtos[" + i + "].";

	}

}