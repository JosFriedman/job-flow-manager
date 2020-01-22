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
import gov.nyc.doitt.jobstatemanager.jobappconfig.JobAppConfig;
import gov.nyc.doitt.jobstatemanager.jobappconfig.JobAppConfigService;
import gov.nyc.doitt.jobstatemanager.jobappconfig.TaskConfig;

@Component
class JobService {

	private Logger logger = LoggerFactory.getLogger(JobService.class);

	@Autowired
	private JobAppConfigService jobAppConfigService;

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
	public JobDto createJob(JobDto jobDto) {

		String appId = jobDto.getAppId();
		if (!jobAppConfigService.existsJobAppConfig(appId)) {
			throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appId=%s", appId));
		}
		String jobId = jobDto.getJobId();
		if (jobRepository.existsByAppIdAndJobId(appId, jobId)) {
			throw new ConflictException(String.format("Job for appId=%s, jobId=%s already exists", appId, jobId));
		}

		Job job = jobDtoMapper.fromDto(jobDto);

		JobAppConfig jobAppConfig = jobAppConfigService.getJobAppConfigDomain(appId);
		TaskConfig taskConfig = jobAppConfig.getTaskConfigs().stream().findFirst()
				.orElseThrow(() -> new JobStateManagerException("JobAppConfig for appId=" + appId + " has no TaskConfigs"));
		job.setNextTaskName(taskConfig.getName());
		jobRepository.save(job);
		return jobDtoMapper.toDto(job);
	}

	/**
	 * Start and return next batch of jobs for appId
	 * 
	 * @param appId
	 * @return
	 */
	List<JobDto> startTaskForJobs(String taskName, String appId) {

//		try {
//			if (!jobAppConfigService.existsJobAppConfig(appId)) {
//				throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appId=%s", appId));
//			}
//
//			JobAppConfig jobAppConfig = jobAppConfigService.getJobAppConfigDomain(appId);
//
//			PageRequest pageRequest = PageRequest.of(0, jobAppConfig.getMaxBatchSize(),
//					Sort.by(Sort.Direction.ASC, "createdTimestamp"));
//
//			List<Job> jobs = jobRepository.findByAppIdAndStateInAndErrorCountLessThan(appId,
//					Arrays.asList(new JobState[] { JobState.READY, JobState.ERROR }), jobAppConfig.getMaxRetriesForError() + 1,
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
	 * Update processing results in jobDtos for jobs specified by appId
	 * 
	 * @param appId
	 * @param jobDto
	 * @return
	 */
	public List<JobDto> endTaskForJobs(String appId, String taskName, List<JobDto> jobDtos) {

		if (!jobAppConfigService.existsJobAppConfig(appId)) {
			throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appId=%s", appId));
		}

		// get jobs from DB
		List<String> jobIds = jobDtos.stream().map(p -> p.getJobId()).collect(Collectors.toList());
		List<Job> jobs = jobRepository.findByAppIdAndJobIdIn(appId, jobIds);
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
	 * Get jobs for appId
	 * 
	 * @param appId
	 * @return
	 */
	List<JobDto> getJobs(String appId, Sort sort) {

		return jobDtoMapper.toDto(jobRepository.findByAppId(appId, sort));
	}

	/**
	 * Get jobs for appId and state
	 * 
	 * @param appId
	 * @return
	 */
	List<JobDto> getJobs(String appId, JobState state, Sort sort) {

		return jobDtoMapper.toDto(jobRepository.findByAppIdAndState(appId, state.name(), sort));
	}

	/**
	 * Update processing results in jobDtos for jobs specified by appId
	 * 
	 * @param appId
	 * @param jobDto
	 * @return
	 */
	public List<JobDto> updateJobsWithResults(String appId, List<JobDto> jobDtos) {

		if (!jobAppConfigService.existsJobAppConfig(appId)) {
			throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appId=%s", appId));
		}

		// get jobs from DB
		List<String> jobIds = jobDtos.stream().map(p -> p.getJobId()).collect(Collectors.toList());
		List<Job> jobs = jobRepository.findByAppIdAndJobIdIn(appId, jobIds);
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
	 * Delete job specified by appId and jobId
	 * 
	 * @param appId
	 * @param jobId
	 * @return
	 */
	public String deleteJob(String appId, String jobId) {

		if (!jobRepository.existsByAppIdAndJobId(appId, jobId)) {
			throw new EntityNotFoundException(String.format("Can't find Job for appId=%s, jobId=%s", appId, jobId));
		}
		jobRepository.deleteByAppIdAndJobId(appId, jobId);
		return appId + "/" + jobId;
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
	 * Get job specified by appId and jobId
	 * 
	 * @param appId
	 * @param jobId
	 * @return
	 */
	public JobDto getJob(String appId, String jobId) {

		Job job = jobRepository.findByAppIdAndJobId(appId, jobId);
		if (job == null) {
			throw new EntityNotFoundException(String.format("Can't find Job for appId=%s, jobId=%s", appId, jobId));
		}
		return jobDtoMapper.toDto(job);

	}

	/**
	 * Update job specified by appId and jobId, from jobDto
	 * 
	 * @param appId
	 * @param jobId
	 * @param jobDto
	 * @return
	 */
	public JobDto updateJob(String appId, String jobId, JobDto jobDto) {

		if (!jobRepository.existsByAppIdAndJobId(appId, jobId)) {
			throw new EntityNotFoundException(String.format("Can't find Job for appId=%s, jobId=%s", appId, jobId));
		}
		Job job = jobRepository.findByAppIdAndJobId(appId, jobId);
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
