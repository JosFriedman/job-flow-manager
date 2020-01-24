package gov.nyc.doitt.jobstatemanager.task;

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
	 * Start taskName tasks for all qualifying jobs for appName
	 * 
	 * @param taskName
	 * @param appName
	 * @return
	 */
	public List<TaskDto> startTasks(String appName, String taskName) {

		if (!jobAppConfigService.existsJobAppConfig(appName)) {
			throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appName=%s", appName));
		}

		// get jobs that are available for this task
		JobAppConfig jobAppConfig = jobAppConfigService.getJobAppConfigDomain(appName);
		TaskConfig taskConfig = jobAppConfig.getTaskConfig(taskName);

		PageRequest pageRequest = PageRequest.of(0, taskConfig.getMaxBatchSize(), Sort.by(Sort.Direction.ASC, "createdTimestamp"));
		List<Job> jobs = jobRepository.findByAppNameAndStateInAndNextTaskName(appName,
				Arrays.asList(new JobState[] { JobState.READY }), taskName, pageRequest);
		logger.info("startTasks: number of jobs found: {}", jobs.size());

		// create tasks and update jobs
		jobs.forEach(p -> startTask(taskName, p));

		// return TaskDtos
		return jobs.stream().map(p -> taskDtoMapper.toDto(p.getJobId(), p.getTasks().get(p.getTasks().size() - 1)))
				.collect(Collectors.toList());
	}

	public List<TaskDto> endTasks(String appName, String taskName, List<TaskDto> taskDtos) {

		if (!jobAppConfigService.existsJobAppConfig(appName)) {
			throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appName=%s", appName));
		}

		// get jobs from DB for jobIds in taskDtos
		List<String> jobIds = taskDtos.stream().map(p -> p.getJobId()).collect(Collectors.toList());
		List<Job> jobs = jobRepository.findByAppNameAndJobIdInAndStateInAndNextTaskName(appName, jobIds,
				Arrays.asList(new JobState[] { JobState.PROCESSING }), taskName);
		if (jobs.size() != jobIds.size()) {
			List<String> foundJobIds = jobs.stream().map(p -> p.getJobId()).collect(Collectors.toList());
			throw new EntityNotFoundException(jobIds.stream().filter(p -> !foundJobIds.contains(p)).map(q -> {
				return "jobId=" + q + " not found or not ready for this task=" + taskName;
			}).collect(Collectors.toList()));
		}
		Map<String, Job> jobIdJobMap = jobs.stream().collect(Collectors.toMap(Job::getJobId, Function.identity()));

		// update jobs and tasks with results
		taskDtos.forEach(p -> endTask(taskName, p, jobIdJobMap.get(p.getJobId()), getNextTaskConfig(appName, taskName)));

		return taskDtos;
	}

	private void startTask(String taskName, Job job) {

		logger.debug("takName={}, job={}", taskName, job.toString());
		job.startTask(new Task(taskName));
		jobRepository.save(job);

	}

	private TaskConfig getNextTaskConfig(String appName, String taskName) {

		JobAppConfig jobAppConfig = jobAppConfigService.getJobAppConfigDomain(appName);
		List<TaskConfig> taskConfigs = jobAppConfig.getTaskConfigs();
		for (int i = 0; i < taskConfigs.size(); i++) {
			TaskConfig taskConfig = taskConfigs.get(i);
			if (taskConfig.getName().equals(taskName)) {
				return i < taskConfigs.size() - 1 ? taskConfigs.get(i + 1) : null;
			}
		}
		throw new JobStateManagerException(
				String.format("Task name '%s' not found in JobAppConfig '%s': " + taskName, jobAppConfig));
	}

	private void endTask(String taskName, TaskDto taskDto, Job job, TaskConfig nextTaskConfig) {

		logger.debug("TaskDto: {}", taskDto.toString());

		if (job.getState() != JobState.PROCESSING) {
			throw new JobStateManagerException("Job is not in correct state for updating with result: " + job);
		}

		// get last task
		Task task = job.getTasks().get(job.getTasks().size() - 1);
		if (!task.getName().equals(taskName)) {
			throw new JobStateManagerException(
					String.format("Given task name '%s' != Task's name '%s': " + taskName, task.getName()));
		}

		taskDtoMapper.fromDtoResult(taskDto, task);

		if (task.getState() == TaskState.COMPLETED) {
			if (nextTaskConfig != null) {
				job.setNextTaskName(nextTaskConfig.getName());
				job.setState(JobState.READY);
			} else {
				job.setNextTaskName(null);
				job.setState(JobState.COMPLETED);
			}
		} else {
			job.setState(JobState.ERROR);
		}
		jobRepository.save(job);
	}
}
