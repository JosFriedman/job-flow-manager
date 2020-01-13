package gov.nyc.doitt.jobstatemanager.domain.job;

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

import gov.nyc.doitt.jobstatemanager.domain.job.dto.JobDto;
import gov.nyc.doitt.jobstatemanager.domain.job.model.Job;
import gov.nyc.doitt.jobstatemanager.domain.job.model.JobState;
import gov.nyc.doitt.jobstatemanager.domain.jobappconfig.JobAppConfigService;
import gov.nyc.doitt.jobstatemanager.domain.jobappconfig.model.JobAppConfig;
import gov.nyc.doitt.jobstatemanager.infrastructure.ConflictException;
import gov.nyc.doitt.jobstatemanager.infrastructure.EntityNotFoundException;
import gov.nyc.doitt.jobstatemanager.infrastructure.JobStateManagerException;

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
		jobRepository.save(job);
		return jobDtoMapper.toDto(job);
	}

	/**
	 * Return next batch of jobs for appId
	 * 
	 * @param appId
	 * @return
	 */
	List<JobDto> getNextBatch(String appId) {

		try {
			JobAppConfig jobAppConfig = jobAppConfigService.getJobAppConfigDomain(appId);

			PageRequest pageRequest = PageRequest.of(0, jobAppConfig.getMaxBatchSize(),
					Sort.by(Sort.Direction.ASC, "createdTimestamp"));

			List<Job> jobs = jobRepository.findByAppIdAndStateInAndErrorCountLessThan(appId,
					Arrays.asList(new JobState[] { JobState.NEW, JobState.ERROR }), jobAppConfig.getMaxRetriesForError() + 1,
					pageRequest);
			logger.info("getNextBatch: number of jobs found: {}", jobs.size());
			if (logger.isDebugEnabled()) {
				jobs.forEach(p -> logger.debug("job: {}", p.toString()));
			}

			// mark each submission as picked up for processing
			jobs.forEach(p -> {
				p.start();
				jobRepository.save(p);
			});
			return jobDtoMapper.toDto(jobs);

		} catch (JobStateManagerException e) {
			throw e;
		} catch (Exception e) {
			throw new JobStateManagerException(e);
		}

	}

	/**
	 * Get jobs for appId
	 * 
	 * @param appId
	 * @return
	 */
	List<JobDto> getJobs(String appId) {

		return jobDtoMapper.toDto(jobRepository.findByAppId(appId));
	}

	/**
	 * Get jobs for appId and state
	 * 
	 * @param appId
	 * @return
	 */
	List<JobDto> getJobs(String appId, JobState state) {

		return jobDtoMapper.toDto(jobRepository.findByAppIdAndState(appId, state.name()));
	}

	/**
	 * Update processing results in jobDtos for jobs specified by appId
	 * 
	 * @param appId
	 * @param jobDto
	 * @return
	 */
	public List<JobDto> updateJobsWithResults(String appId, List<JobDto> jobDtos) {

		// get jobs from DB
		List<String> jobIds = jobDtos.stream().map(p -> p.getJobId()).collect(Collectors.toList());
		List<Job> jobs = jobRepository.findByAppIdAndJobIdIn(appId, jobIds);
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

		Job job = jobRepository.findByAppIdAndJobId(appId, jobId);
		if (job == null) {
			throw new EntityNotFoundException(String.format("Can't find Job for appId=%s, jobId=%s", appId, jobId));
		}
		return jobRepository.deleteByAppIdAndJobId(appId, jobId);
	}

	/**
	 * Get all jobs
	 * 
	 * @return
	 */
	public List<JobDto> getJobs() {

		return jobDtoMapper.toDto(jobRepository.findAllByOrderByAppIdAsc());
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
	 * Return job ids for appId
	 */
	public List<String> getJobIds(String appId, boolean nextBatch) {

		List<JobDto> jobDtos = nextBatch ? getNextBatch(appId) : getJobs(appId);
		return jobDtos.stream().map(p -> p.getJobId()).collect(Collectors.toList());

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

		Job job = jobRepository.getByAppIdAndJobId(appId, jobId);
		jobDtoMapper.fromDto(jobDto, job);

		jobRepository.save(job);
		return jobDtoMapper.toDto(job);
	}

	// Update processing result in job from jobDto
	private JobDto updateJobWithResult(Job job, JobDto jobDto) {

		jobDtoMapper.fromDtoResult(jobDto, job);
		jobRepository.save(job);
		return jobDtoMapper.toDto(job);
	}

}
