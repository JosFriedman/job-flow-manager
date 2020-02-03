package gov.nyc.doitt.jobstatemanager.task;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
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
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfig;
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfigService;
import gov.nyc.doitt.jobstatemanager.jobconfig.TaskConfig;

@Component
class TaskService {

	private Logger logger = LoggerFactory.getLogger(TaskService.class);

	@Autowired
	private JobConfigService jobConfigService;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private TaskDtoMapper taskDtoMapper;

	/**
	 * Start taskName tasks for all qualifying jobs for jobName
	 * 
	 * @param taskName
	 * @param jobName
	 * @return
	 */
	public List<TaskDto> startTasks(String jobName, String taskName) {

		if (!jobConfigService.existsJobConfig(jobName)) {
			throw new EntityNotFoundException(String.format("Can't find JobConfig for jobName=%s", jobName));
		}

		// get jobs that are available for this task
		JobConfig jobConfig = jobConfigService.getJobConfigDomain(jobName);
		TaskConfig taskConfig = jobConfig.getTaskConfig(taskName);

		PageRequest pageRequest = PageRequest.of(0, taskConfig.getMaxBatchSize(), Sort.by(Sort.Direction.ASC, "createdTimestamp"));
		List<Job> jobs = jobRepository.findByJobNameAndStateInAndNextTaskName(jobName,
				Arrays.asList(new JobState[] { JobState.READY }), taskName, pageRequest);
		logger.info("startTasks: number of jobs found: {}", jobs.size());

		// create tasks and update jobs
		jobs.forEach(p -> startTask(taskName, p));

		// return last task for each job in list of TaskDtos
		return jobs.stream().map(p -> taskDtoMapper.toDto(p, p.getLastTask())).collect(Collectors.toList());
	}

	public List<TaskDto> endTasks(String jobName, String taskName, List<TaskDto> taskDtos) {

		if (!jobConfigService.existsJobConfig(jobName)) {
			throw new EntityNotFoundException(String.format("Can't find JobConfig for jobName=%s", jobName));
		}

		// get jobs from DB for jobIds in taskDtos
		List<String> jobIds = taskDtos.stream().map(p -> p.getJobId()).collect(Collectors.toList());
		List<Job> jobs = jobRepository.findByJobNameAndJobIdInAndStateInAndNextTaskName(jobName, jobIds,
				Arrays.asList(new JobState[] { JobState.PROCESSING }), taskName);
		if (jobs.size() != jobIds.size()) {
			List<String> foundJobIds = jobs.stream().map(p -> p.getJobId()).collect(Collectors.toList());
			throw new EntityNotFoundException(jobIds.stream().filter(p -> !foundJobIds.contains(p)).map(q -> {
				return "jobId=" + q + " not found or not processing this task=" + taskName;
			}).collect(Collectors.toList()));
		}
		Map<String, Job> jobIdJobMap = jobs.stream().collect(Collectors.toMap(Job::getJobId, Function.identity()));

		// update jobs and tasks with results
		Pair<TaskConfig, TaskConfig> currentAndNextTaskConfigs = getCurrentAndNextTaskConfigs(jobName, taskName);
		taskDtos.forEach(p -> endTask(taskName, jobIdJobMap.get(p.getJobId()), p, currentAndNextTaskConfigs.getLeft(),
				currentAndNextTaskConfigs.getRight()));

		return taskDtoMapper.toDto(jobs, taskName);
	}

	private void startTask(String taskName, Job job) {

		logger.debug("taskName={}, job={}", taskName, job.toString());
		job.startTask(new Task(taskName));
		jobRepository.save(job);

	}

	private Pair<TaskConfig, TaskConfig> getCurrentAndNextTaskConfigs(String jobName, String taskName) {

		JobConfig jobConfig = jobConfigService.getJobConfigDomain(jobName);
		List<TaskConfig> taskConfigs = jobConfig.getTaskConfigs();
		for (int i = 0; i < taskConfigs.size(); i++) {
			TaskConfig taskConfig = taskConfigs.get(i);
			if (taskConfig.getName().equals(taskName)) {
				return Pair.of(taskConfig, i < taskConfigs.size() - 1 ? taskConfigs.get(i + 1) : null);
			}
		}
		throw new JobStateManagerException(String.format("Task name '%s' not found in JobConfig '%s': " + taskName, jobConfig));
	}

	private void endTask(String taskName, Job job, TaskDto taskDto, TaskConfig currentTaskConfig, TaskConfig nextTaskConfig) {

		logger.debug("taskDto: {}", taskDto);

		if (job.getState() != JobState.PROCESSING) {
			throw new JobStateManagerException("Job is not in correct state for updating with result: " + job);
		}
		Task task = job.getLastTask(taskName);
		taskDtoMapper.fromDtoResult(taskDto, task);

		if (task.getState() == TaskState.COMPLETED) {
			if (nextTaskConfig != null) {
				job.setNextTaskName(nextTaskConfig.getName());
				job.setState(JobState.READY);
			} else {
				job.setNextTaskName(null);
				job.setState(JobState.COMPLETED);
			}
		} else if (job.getTotalErrorCountForTask(taskName) > currentTaskConfig.getMaxRetriesForError()) {
			job.setState(JobState.ERROR);
		} else {
			job.setState(JobState.READY);
		}

		jobRepository.save(job);
	}
}