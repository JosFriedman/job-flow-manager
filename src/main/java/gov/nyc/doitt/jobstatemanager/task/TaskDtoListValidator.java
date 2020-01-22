package gov.nyc.doitt.jobstatemanager.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates the TaskDto payload in REST calls
 */
@Component
class TaskDtoListValidator implements Validator {

	@Autowired
	private TaskDtoValidator taskDtoValidator;

	@Override
	public boolean supports(Class<?> clazz) {
		return java.util.ArrayList.class.isAssignableFrom(clazz);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void validate(Object target, Errors errors) {

		List l = (java.util.ArrayList) target;
		if (l.size() == 0 || !(l.get(0) instanceof TaskDto)) {
			return;
		}
		List<TaskDto> taskDtos = (List<TaskDto>) l;

		for (int i = 0; i < taskDtos.size(); i++) {
			TaskDto taskDto = taskDtos.get(i);
			Object[] validationHints = new Integer[1];
			validationHints[0] = i;
			Errors errors1 = new BeanPropertyBindingResult(taskDto, errors.getObjectName());
			ValidationUtils.invokeValidator(taskDtoValidator, taskDto, errors1, validationHints);
			errors.addAllErrors(errors1);
		}
	}

}