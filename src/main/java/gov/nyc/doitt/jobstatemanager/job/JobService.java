package gov.nyc.doitt.jobstatemanager.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import gov.nyc.doitt.jobstatemanager.common.ConflictException;
import gov.nyc.doitt.jobstatemanager.common.EntityNotFoundException;
import gov.nyc.doitt.jobstatemanager.common.JobStateManagerException;
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfig;
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfigService;
import gov.nyc.doitt.jobstatemanager.jobconfig.TaskConfig;

@Component
class JobService {

	private Logger logger = LoggerFactory.getLogger(JobService.class);

	@Autowired
	private JobConfigService jobConfigService;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobDtoMapper jobDtoMapper;

	/**
	 * Create job from jobDto
	 * 
	 * @param jobDto
	 * @return
	 */
	public JobDto createJob(String jobName, JobDto jobDto) {

		if (!jobConfigService.existsJobConfig(jobName)) {
			throw new EntityNotFoundException(String.format("Can't find JobConfig for jobName=%s", jobName));
		}
		String jobId = jobDto.getJobId();
		if (jobRepository.existsByJobNameAndJobId(jobName, jobId)) {
			throw new ConflictException(String.format("Job for jobName=%s, jobId=%s already exists", jobName, jobId));
		}

		Job job = jobDtoMapper.fromDto(jobName, jobDto);

		JobConfig jobConfig = jobConfigService.getJobConfigDomain(jobName);
		TaskConfig taskConfig = jobConfig.getTaskConfigs().stream().findFirst()
				.orElseThrow(() -> new JobStateManagerException("JobConfig for jobName=" + jobName + " has no TaskConfigs"));
		job.setNextTaskName(taskConfig.getName());
		jobRepository.save(job);
		return jobDtoMapper.toDto(job);
	}

	/**
	 * Start and return next batch of jobs for jobName
	 * 
	 * @param jobName
	 * @return
	 */
	List<JobDto> startTaskForJobs(String taskName, String jobName) {

//		try {
//			if (!jobConfigService.existsJobConfig(jobName)) {
//				throw new EntityNotFoundException(String.format("Can't find JobConfig for jobName=%s", jobName));
//			}
//
//			JobConfig jobConfig = jobConfigService.getJobConfigDomain(jobName);
//
//			PageRequest pageRequest = PageRequest.of(0, jobConfig.getMaxBatchSize(),
//					Sort.by(Sort.Direction.ASC, "createdTimestamp"));
//
//			List<Job> jobs = jobRepository.findByJobNameAndStateInAndErrorCountLessThan(jobName,
//					Arrays.asList(new JobState[] { JobState.READY, JobState.ERROR }), jobConfig.getMaxRetriesForError() + 1,
//					pageRequest);
//			logger.info("getNextBatch: number of jobs found: {}", jobs.size());
//			if (logger.isDebugEnabled()) {
//				jobs.forEach(p -> logger.debug("job: {}", p.toString()));
//			}
//
//			// mark each submission as picked up for processing
//			jobs.forEach(p -> {
////				p.start();
//				jobRepository.save(p);
//			});
//			return jobDtoMapper.toDto(jobs);
//
//		} catch (JobStateManagerException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new JobStateManagerException(e);
//		}
//
		return null;
	}

	/**
	 * Update processing results in jobDtos for jobs specified by jobName
	 * 
	 * @param jobName
	 * @param jobDto
	 * @return
	 */
	public List<JobDto> endTaskForJobs(String jobName, String taskName, List<JobDto> jobDtos) {

		if (!jobConfigService.existsJobConfig(jobName)) {
			throw new EntityNotFoundException(String.format("Can't find JobConfig for jobName=%s", jobName));
		}

		// get jobs from DB
		List<String> jobIds = jobDtos.stream().map(p -> p.getJobId()).collect(Collectors.toList());
		List<Job> jobs = jobRepository.findByJobNameAndJobIdIn(jobName, jobIds);
		if (jobs.size() != jobIds.size()) {
			List<String> foundJobIds = jobs.stream().map(p -> p.getJobId()).collect(Collectors.toList());
			throw new EntityNotFoundException(jobIds.stream().filter(p -> !foundJobIds.contains(p)).collect(Collectors.toList()));
		}
		Map<String, Job> jobIdJobMap = jobs.stream().collect(Collectors.toMap(Job::getJobId, Function.identity()));

		// update results
		List<JobDto> returnJobDtos = new ArrayList<>();
		jobDtos.forEach(p -> {
			Job job = jobIdJobMap.get(p.getJobId());
			JobDto jobDto = updateJobWithResult(job, p);
			returnJobDtos.add(jobDto);
		});

		return returnJobDtos;
	}

	/**
	 * Get jobs for jobName
	 * 
	 * @param jobName
	 * @return
	 */
	List<JobDto> getJobs(String jobName, Sort sort) {

		return jobDtoMapper.toDto(jobRepository.findByJobName(jobName, sort));
	}

	/**
	 * Get jobs for jobName and state
	 * 
	 * @param jobName
	 * @return
	 */
	List<JobDto> getJobs(String jobName, JobState state, Sort sort) {

		return jobDtoMapper.toDto(jobRepository.findByJobNameAndState(jobName, state.name(), sort));
	}

	/**
	 * Update processing results in jobDtos for jobs specified by jobName
	 * 
	 * @param jobName
	 * @param jobDto
	 * @return
	 */
	public List<JobDto> updateJobsWithResults(String jobName, List<JobDto> jobDtos) {

		if (!jobConfigService.existsJobConfig(jobName)) {
			throw new EntityNotFoundException(String.format("Can't find JobConfig for jobName=%s", jobName));
		}

		// get jobs from DB
		List<String> jobIds = jobDtos.stream().map(p -> p.getJobId()).collect(Collectors.toList());
		List<Job> jobs = jobRepository.findByJobNameAndJobIdIn(jobName, jobIds);
		if (jobs.size() != jobIds.size()) {
			List<String> foundJobIds = jobs.stream().map(p -> p.getJobId()).collect(Collectors.toList());
			throw new EntityNotFoundException(jobIds.stream().filter(p -> !foundJobIds.contains(p)).collect(Collectors.toList()));
		}
		Map<String, Job> jobIdJobMap = jobs.stream().collect(Collectors.toMap(Job::getJobId, Function.identity()));

		// update results
		List<JobDto> returnJobDtos = new ArrayList<>();
		jobDtos.forEach(p -> {
			Job job = jobIdJobMap.get(p.getJobId());
			JobDto jobDto = updateJobWithResult(job, p);
			returnJobDtos.add(jobDto);
		});

		return returnJobDtos;
	}

	/**
	 * Delete job specified by jobName and jobId
	 * 
	 * @param jobName
	 * @param jobId
	 * @return
	 */
	public String deleteJob(String jobName, String jobId) {

		if (!jobRepository.existsByJobNameAndJobId(jobName, jobId)) {
			throw new EntityNotFoundException(String.format("Can't find Job for jobName=%s, jobId=%s", jobName, jobId));
		}
		jobRepository.deleteByJobNameAndJobId(jobName, jobId);
		return jobName + "/" + jobId;
	}

	/**
	 * Get all jobs
	 * 
	 * @return
	 */
	public List<JobDto> getJobs(Sort sort) {

		return jobDtoMapper.toDto(jobRepository.findAll(sort));
	}

	/**
	 * Get job specified by jobName and jobId
	 * 
	 * @param jobName
	 * @param jobId
	 * @return
	 */
	public JobDto getJob(String jobName, String jobId) {

		Job job = jobRepository.findByJobNameAndJobId(jobName, jobId);
		if (job == null) {
			throw new EntityNotFoundException(String.format("Can't find Job for jobName=%s, jobId=%s", jobName, jobId));
		}
		return jobDtoMapper.toDto(job);

	}

	/**
	 * Update job specified by jobName and jobId, from jobDto
	 * 
	 * @param jobName
	 * @param jobId
	 * @param jobDto
	 * @return
	 */
	public JobDto updateJob(String jobName, String jobId, JobDto jobDto) {

		if (!jobRepository.existsByJobNameAndJobId(jobName, jobId)) {
			throw new EntityNotFoundException(String.format("Can't find Job for jobName=%s, jobId=%s", jobName, jobId));
		}
		Job job = jobRepository.findByJobNameAndJobId(jobName, jobId);
		jobDtoMapper.fromDto(jobDto, job);

		jobRepository.save(job);
		return jobDtoMapper.toDto(job);
	}

	// Update processing result in job from jobDto
	private JobDto updateJobWithResult(Job job, JobDto jobDto) {

//		jobDtoMapper.fromDtoResult(jobDto, job);
//		jobRepository.save(job);
		return jobDtoMapper.toDto(job);
	}

}
