package gov.nyc.doitt.jobstatemanager.domain.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import gov.nyc.doitt.jobstatemanager.domain.job.dto.JobDto;
import gov.nyc.doitt.jobstatemanager.domain.job.model.Job;
import gov.nyc.doitt.jobstatemanager.domain.job.model.JobState;
import gov.nyc.doitt.jobstatemanager.infrastructure.JobStateManagerException;

@Component
public class JobService {

	private Logger logger = LoggerFactory.getLogger(JobService.class);

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobDtoMapper jobDtoMapper;

	@Value("${jobstatemanager.domain.job.JobService.maxBatchSize}")
	private int maxBatchSize;

	@Value("${jobstatemanager.domain.job.JobService.maxRetriesForError}")
	private int maxRetriesForError;

	private PageRequest pageRequest;

	@PostConstruct
	private void postConstruct() {
		pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "createdTimestamp"));
	}

	/**
	 * Create job from jobDto
	 * 
	 * @param jobDto
	 * @return
	 */
	public JobDto createJob(JobDto jobDto) {

		Job job = jobDtoMapper.fromDto(jobDto);
		jobRepository.save(job);
		return jobDtoMapper.toDto(job);
	}

	/**
	 * Return jobs for appId; if nextBatch = true, return only batch-size number of jobs
	 * 
	 * @param appId
	 * @param nextBatch
	 * @return
	 */
	public List<JobDto> getJobs(String appId, boolean nextBatch) {

		return nextBatch ? getNextBatch(appId) : getJobs(appId);
	}

	/**
	 * Return next batch of jobs for appId
	 * 
	 * @param appId
	 * @return
	 */
	List<JobDto> getNextBatch(String appId) {

		try {
			List<Job> jobs = jobRepository.findByAppIdAndStateInAndErrorCountLessThan(appId,
					Arrays.asList(new JobState[] { JobState.NEW, JobState.ERROR }), maxRetriesForError + 1, pageRequest);
			logger.info("getNextBatch: number of submissions found: {}", jobs.size());

			// mark each submission as picked up for processing
			jobs.forEach(p -> {
				p.startProcessing();
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

		try {
			return jobDtoMapper.toDto(jobRepository.findByAppId(appId));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Update processing results in jobDto for job specified by appId
	 * 
	 * @param appId
	 * @param jobDto
	 * @return
	 */
	public List<JobDto> updateJobsWithResults(String appId, List<JobDto> jobDtos) {

		List<JobDto> returnJobDtos = new ArrayList<>();
		List<String> jobIds = jobDtos.stream().map(p -> p.getJobId()).collect(Collectors.toList());
		List<Job> jobs = jobRepository.getByAppIdAndJobIdIn(appId, jobIds);

		Map<String, Job> jobIdJobMap = jobs.stream().collect(Collectors.toMap(Job::getJobId, Function.identity()));

		jobDtos.forEach(p -> {
			Job job = jobIdJobMap.get(p.getJobId());
			jobDtoMapper.fromDtoPatch(p, job);
			jobRepository.save(job);
			JobDto jobDto = jobDtoMapper.toDto(job);
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

		return jobDtoMapper.toDto(jobRepository.findAll());
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

}
