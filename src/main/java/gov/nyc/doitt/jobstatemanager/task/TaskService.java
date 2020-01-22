package gov.nyc.doitt.jobstatemanager.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nyc.doitt.jobstatemanager.common.EntityNotFoundException;
import gov.nyc.doitt.jobstatemanager.jobappconfig.JobAppConfig;
import gov.nyc.doitt.jobstatemanager.jobappconfig.JobAppConfigService;

@Component
class TaskService {

	private Logger logger = LoggerFactory.getLogger(TaskService.class);

	@Autowired
	private JobAppConfigService jobAppConfigService;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private TaskDtoMapper taskDtoMapper;

	/**
	 * Start taskName tasks for all qualifying jobs for appId
	 * 
	 * @param taskName
	 * @param appId
	 * @return
	 */
	List<TaskDto> startTasks(String taskName, String appId) {

		if (!jobAppConfigService.existsJobAppConfig(appId)) {
			throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appId=%s", appId));
		}

		JobAppConfig jobAppConfig = jobAppConfigService.getJobAppConfigDomain(appId);

//		PageRequest pageRequest = PageRequest.of(0, jobAppConfig.getMaxBatchSize(),
//				Sort.by(Sort.Direction.ASC, "createdTimestamp"));
//
//		List<Job> jobs = taskRepository.findByAppIdAndStateInAndErrorCountLessThan(appId,
//				Arrays.asList(new TaskState[] { TaskState.NEW, TaskState.ERROR }), jobAppConfig.getMaxRetriesForError() + 1,
//				pageRequest);
//		logger.info("getNextBatch: number of jobs found: {}", jobs.size());
//		if (logger.isDebugEnabled()) {
//			jobs.forEach(p -> logger.debug("job: {}", p.toString()));
//		}
//
//		// mark each submission as picked up for processing
//		jobs.forEach(p -> {
//			p.start();
//			taskRepository.save(p);
//		});
//		return taskDtoMapper.toDto(jobs);

		return null;
	}

}
