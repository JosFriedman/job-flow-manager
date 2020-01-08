package gov.nyc.doitt.jobstatemanager.domain.job;

import java.sql.Timestamp;
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
import gov.nyc.doitt.jobstatemanager.infrastructure.JobStateManagerConcurrencyException;
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

//	@Transactional(transactionManager = "jobManagerTransactionManager")
	public List<JobDto> getJobStates() {

//		try {
//		jobRepository.findAll();
		return jobDtoMapper.toDto(jobRepository.findAll());
//		} catch (Throwable e) {
//			return null;
//		}
	}

//	@Transactional(transactionManager = "jobManagerTransactionManager")
	public List<JobDto> getJobStates(String appId) {

		try {
			return jobDtoMapper.toDto(jobRepository.findByAppId(appId));
		} catch (Exception e) {
			return null;
		}
	}

//	@Transactional(transactionManager = "jobManagerTransactionManager")
	public JobDto getJobState(String appId, String jobId) {

		Job job = jobRepository.findByAppIdAndJobId(appId, jobId);
		if (job == null) {
			throw new EntityNotFoundException(String.format("Can't find Job for appId=%s, jobId=%s", appId, jobId));
		}
		return jobDtoMapper.toDto(job);

	}

	/**
	 * Return next batch of job states
	 * 
	 * @return
	 */
//	@Transactional(transactionManager = "jobManagerTransactionManager")
	public List<JobDto> getNextBatch(String appId) {

		try {
			List<Job> jobs = jobRepository.findByAppIdAndStateInAndErrorCountLessThan(appId,
					Arrays.asList(new JobState[] { JobState.NEW, JobState.ERROR }), maxRetriesForError + 1, pageRequest);
			logger.info("getNextBatch: number of submissions found: {}", jobs.size());

			// mark each submission as picked up for processing
			jobs.forEach(p -> {
				p.setState(JobState.PROCESSING);
				p.setStartTimestamp(new Timestamp(System.currentTimeMillis()));
				jobRepository.save(p);
			});
			return jobDtoMapper.toDto(jobs);

		} catch (JobStateManagerException e) {
			throw e;
		} catch (Exception e) {
			throw new JobStateManagerConcurrencyException(e);
		}

	}

	/**
	 * Return job ids for appId
	 * 
	 * @return
	 */
//	@Transactional(transactionManager = "jobManagerTransactionManager")
	public List<String> getJobIds(String appId, boolean nextBatch) {

		List<JobDto> jobDtos = nextBatch ? getNextBatch(appId) : getJobStates(appId);
		return jobDtos.stream().map(p -> p.getJobId()).collect(Collectors.toList());

	}

	/**
	 * Return job flows for appId
	 * 
	 * @return
	 */
//	@Transactional(transactionManager = "jobManagerTransactionManager")
	public List<JobDto> getJobStates(String appId, boolean nextBatch) {

		return nextBatch ? getNextBatch(appId) : getJobStates(appId);
	}

//	@Transactional("jobManagerTransactionManager")
	public JobDto createJobState(JobDto jobDto) {

		Job job = jobDtoMapper.fromDto(jobDto);
		job.setState(JobState.NEW);
		jobRepository.save(job);
		return jobDtoMapper.toDto(job);
	}

//	@Transactional("jobManagerTransactionManager")
	public JobDto updateJobState(String appId, String jobId, JobDto jobDto) {

		if (!jobRepository.existsByAppIdAndJobId(appId, jobId)) {
			throw new EntityNotFoundException("Can't find Job: " + appId + ", " + jobId);
		}

		Job job = jobRepository.getByAppIdAndJobId(appId, jobId);
		jobDtoMapper.fromDto(jobDto, job);

		jobRepository.save(job);
		return jobDtoMapper.toDto(job);
	}

//	@Transactional("jobManagerTransactionManager")
	public List<JobDto> patchJobStates(String appId, List<JobDto> jobDtos) {

		List<JobDto> returnJobDtos = new ArrayList<>();
		List<String> jobIds = jobDtos.stream().map(p -> p.getJobId()).collect(Collectors.toList());
		List<Job> jobs = jobRepository.getByAppIdAndJobIdIn(appId, jobIds);

//		Map<String, String> jobIdStatusMap = jobDtos.stream()
//				.collect(Collectors.toMap(JobDto::getJobId, JobDto::getStatus));
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

//	@Transactional(transactionManager = "jobManagerTransactionManager")
	public String deleteJobState(String appId, String jobId) {

		Job job = jobRepository.findByAppIdAndJobId(appId, jobId);
		if (job == null) {
			throw new EntityNotFoundException(String.format("Can't find Job for appId=%s, jobId=%s", appId, jobId));
		}

		return jobRepository.deleteByAppIdAndJobId(appId, jobId);
	}

}
