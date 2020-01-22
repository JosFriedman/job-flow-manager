package gov.nyc.doitt.jobstatemanager.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import gov.nyc.doitt.jobstatemanager.common.EntityNotFoundException;
import gov.nyc.doitt.jobstatemanager.common.JobStateManagerException;
import gov.nyc.doitt.jobstatemanager.job.Job;
import gov.nyc.doitt.jobstatemanager.job.JobRepository;
import gov.nyc.doitt.jobstatemanager.job.JobState;
import gov.nyc.doitt.jobstatemanager.jobappconfig.JobAppConfig;
import gov.nyc.doitt.jobstatemanager.jobappconfig.JobAppConfigService;
import gov.nyc.doitt.jobstatemanager.jobappconfig.TaskConfig;

@Component
class TaskService {

	private Logger logger = LoggerFactory.getLogger(TaskService.class);

	@Autowired
	private JobAppConfigService jobAppConfigService;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private TaskDtoMapper taskDtoMapper;

	/**
	 * Start taskName tasks for all qualifying jobs for appId
	 * 
	 * @param taskName
	 * @param appId
	 * @return
	 */
	List<TaskDto> startTasks(String appId, String taskName) {

		if (!jobAppConfigService.existsJobAppConfig(appId)) {
			throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appId=%s", appId));
		}

		JobAppConfig jobAppConfig = jobAppConfigService.getJobAppConfigDomain(appId);

		PageRequest pageRequest = PageRequest.of(0, jobAppConfig.getMaxBatchSize(),
				Sort.by(Sort.Direction.ASC, "createdTimestamp"));

		List<Job> jobs = jobRepository.findByAppIdAndStateInAndNextTaskName(appId, Arrays.asList(new JobState[] { JobState.READY }),
				taskName, pageRequest);
		logger.info("startTasks: number of jobs found: {}", jobs.size());

		List<TaskDto> taskDtos = new ArrayList<>();
		jobs.forEach(p -> {

			logger.debug("job: {}", p.toString());

			Task task = new Task(taskName);
			taskDtos.add(taskDtoMapper.toDto(p.getJobId(), task));
			p.startTask(task);
			jobRepository.save(p);
		});
		return taskDtos;
	}

	public List<TaskDto> endTasks(String appId, String taskName, List<TaskDto> taskDtos) {

		if (!jobAppConfigService.existsJobAppConfig(appId)) {
			throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appId=%s", appId));
		}

		// get jobs from DB
		List<String> jobIds = taskDtos.stream().map(p -> p.getJobId()).collect(Collectors.toList());

		List<Job> jobs = jobRepository.findByAppIdAndJobIdInAndStateInAndNextTaskName(appId, jobIds,
				Arrays.asList(new JobState[] { JobState.PROCESSING }), taskName);

		if (jobs.size() != jobIds.size()) {
			List<String> foundJobIds = jobs.stream().map(p -> p.getJobId()).collect(Collectors.toList());
			throw new EntityNotFoundException(jobIds.stream().filter(p -> !foundJobIds.contains(p)).collect(Collectors.toList()));
		}
		Map<String, Job> jobIdJobMap = jobs.stream().collect(Collectors.toMap(Job::getJobId, Function.identity()));

		JobAppConfig jobAppConfig = jobAppConfigService.getJobAppConfigDomain(appId);
		List<TaskConfig> taskConfigs = jobAppConfig.getTaskConfigs();
		TaskConfig nextTaskConfig = null;
		for (int i = 0; i < taskConfigs.size(); i++) {
			TaskConfig taskConfig = taskConfigs.get(i);
			if (taskConfig.getName().equals(taskName)) {
				if (i < taskConfigs.size() - 1) {
					nextTaskConfig = taskConfigs.get(i + 1);
					break;
				}
			}
		}
		final TaskConfig nextTaskConfig1 = nextTaskConfig;
		// update results
		taskDtos.forEach(p -> {

			Job job = jobIdJobMap.get(p.getJobId());
			Task task = job.getTasks().get(job.getTasks().size() - 1);

			taskDtoMapper.fromDtoResult(p, task);
			
			if (task.getState() == TaskState.COMPLETED) {
				if (nextTaskConfig1 != null) {
					job.setNextTaskName(nextTaskConfig1.getName());
					job.setState(JobState.READY);
				} else {
					job.setState(JobState.COMPLETED);
				}
			} else {
				job.setState(JobState.ERROR);
				
			}
			jobRepository.save(job);
		});

		return taskDtos;
	}

}
