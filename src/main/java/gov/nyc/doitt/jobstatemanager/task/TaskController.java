package gov.nyc.doitt.jobstatemanager.task;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tasks")
public class TaskController {

	private Logger logger = LoggerFactory.getLogger(TaskController.class);

	@Autowired
	private TaskService taskService;

	@Autowired
	private TaskDtoValidator taskDtoValidator;

	@Autowired
	private TaskDtoListValidator taskDtoListValidator;

//	@InitBinder("jobDto")
//	private void initBinder_jobDto(WebDataBinder binder) {
//		binder.addValidators(taskDtoValidator);
//	}
//
//	@InitBinder("jobDtoList")
//	private void initBinder_jobDtoList(WebDataBinder binder) {
//		binder.addValidators(taskDtoListValidator);
//	}

	@PostMapping("")
	public List<TaskDto> startTasks(@RequestParam String appId, @RequestParam String taskName) {

		logger.debug("startTasks: entering: appId={}, taskName={}", appId, taskName);
		return taskService.startTasks(appId, taskName);
	}

	@PutMapping("")
	public List<TaskDto> endTasks(@RequestParam String appId, @RequestParam String taskName,
			@Valid @RequestBody List<TaskDto> taskDtos, BindingResult result) {

		logger.debug("endTasks: entering: appId={}, taskName={}, taskDtos{}", appId, taskName, taskDtos);
		return taskService.endTasks(appId, taskName, taskDtos);
	}

//
//	@PutMapping("/tasks/{taskName}")
//	public List<JobDto> endTaskForJobs(@PathVariable String taskName, @RequestParam String appId,
//			@Valid @RequestBody List<JobDto> jobDtos, BindingResult result) {
//
//		if (logger.isDebugEnabled()) {
//			logger.debug("endTaskForJobs: entering: taskName={}, appId={}", taskName, appId);
//			if (logger.isDebugEnabled()) {
//				jobDtos.forEach(p -> logger.debug("job: {}", p));
//			}
//		}
//
//		if (result.hasErrors()) {
//			throw new ValidationException(result.getFieldErrors());
//		}
//		return taskService.endTaskForJobs(appId, taskName, jobDtos);
//	}
//
}
