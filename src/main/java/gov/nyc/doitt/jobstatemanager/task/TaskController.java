package gov.nyc.doitt.jobstatemanager.task;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nyc.doitt.jobstatemanager.common.ValidationException;

@RestController
@RequestMapping("jobs")
public class TaskController {

	private Logger logger = LoggerFactory.getLogger(TaskController.class);

	@Autowired
	private TaskService taskService;

	@Autowired
	private TaskDtoValidator taskDtoValidator;

	@Autowired
	private TaskDtoListValidator taskDtoListValidator;

	@InitBinder("taskDto")
	private void initBinder_taskDto(WebDataBinder binder) {
		binder.addValidators(taskDtoValidator);
	}

	@InitBinder("taskDtoList")
	private void initBinder_taskDtoList(WebDataBinder binder) {
		binder.addValidators(taskDtoListValidator);
	}

	@PostMapping("/{appName}/tasks/{taskName}")
	public List<TaskDto> startTasks(@PathVariable String appName, @PathVariable String taskName) {

		logger.debug("startTasks: entering: appName={}, taskName={}", appName, taskName);
		return taskService.startTasks(appName, taskName);
	}

	@PutMapping("/{appName}/tasks/{taskName}")
	public List<TaskDto> endTasks(@PathVariable String appName, @PathVariable String taskName,
			@Valid @RequestBody List<TaskDto> taskDtos, BindingResult result) {

		if (logger.isDebugEnabled()) {
			logger.debug("endTasks: entering: appName={}, taskName={}", appName, taskName);
			if (logger.isDebugEnabled()) {
				taskDtos.forEach(p -> logger.debug("taskDto: {}", p));
			}
		}

		if (result.hasErrors()) {
			throw new ValidationException(result.getFieldErrors());
		}

		return taskService.endTasks(appName, taskName, taskDtos);
	}

}
