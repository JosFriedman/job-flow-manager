package gov.nyc.doitt.jobstatemanager.domain.job;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import gov.nyc.doitt.jobstatemanager.domain.job.dto.JobDto;

/**
 * Validates the JobDto payload in REST calls
 */
@Component
public class JobDtoValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return JobDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
	}

}