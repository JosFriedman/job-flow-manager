package gov.nyc.doitt.jobstatemanager.job;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates the JobDto payload in REST calls
 */
@Component
class JobDtoListValidator implements Validator {

	@Autowired
	private JobDtoValidator jobDtoValidator;

	@Override
	public boolean supports(Class<?> clazz) {
		return java.util.ArrayList.class.isAssignableFrom(clazz);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void validate(Object target, Errors errors) {

		List l = (java.util.ArrayList) target;
		if (l.size() == 0 || !(l.get(0) instanceof JobDto)) {
			return;
		}
		List<JobDto> jobDtos = (List<JobDto>) l;

		for (int i = 0; i < jobDtos.size(); i++) {
			JobDto jobDto = jobDtos.get(i);
			Object[] validationHints = new Integer[1];
			validationHints[0] = i;
			Errors errors1 = new BeanPropertyBindingResult(jobDto, errors.getObjectName());
			ValidationUtils.invokeValidator(jobDtoValidator, jobDto, errors1, validationHints);
			errors.addAllErrors(errors1);
		}
	}

}