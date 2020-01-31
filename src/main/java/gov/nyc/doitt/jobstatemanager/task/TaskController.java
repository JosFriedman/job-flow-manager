package gov.nyc.doitt.jobstatemanager.task;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.nyc.doitt.jobstatemanager.common.ValidationException;

@RestController
@RequestMapping("tasks")
@PreAuthorize("hasRole('ROLE_ADMIN')")
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

	@PostMapping(params = { "jobName", "taskName" })
	public List<TaskDto> startTasks(@RequestParam String jobName, @RequestParam String taskName) {

		logger.debug("startTasks: entering: jobName={}, taskName={}", jobName, taskName);
		return taskService.startTasks(jobName, taskName);
	}

//	@PutMapping(params = { "jobName", "taskName" })
	@RequestMapping(method = RequestMethod.PUT, params = { "jobName", "taskName" })
	public List<TaskDto> endTasks(@RequestParam String jobName, @RequestParam String taskName,
			@Valid @RequestBody List<TaskDto> taskDtos, BindingResult result) {

		if (logger.isDebugEnabled()) {
			logger.debug("endTasks: entering: jobName={}, taskName={}", jobName, taskName);
			taskDtos.forEach(p -> logger.debug("taskDto: {}", p));
		}

		if (result.hasErrors()) {
			throw new ValidationException(result.getFieldErrors());
		}

		return taskService.endTasks(jobName, taskName, taskDtos);
	}

}
