package gov.nyc.doitt.jobstatemanager.domain.jobstate;

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

import gov.nyc.doitt.jobstatemanager.domain.jobstate.dto.JobStateDto;
import gov.nyc.doitt.jobstatemanager.domain.jobstate.model.JobState;
import gov.nyc.doitt.jobstatemanager.domain.jobstate.model.JobStatus;
import gov.nyc.doitt.jobstatemanager.infrastructure.JobStateManagerConcurrencyException;
import gov.nyc.doitt.jobstatemanager.infrastructure.JobStateManagerException;

@Component
public class JobStateService {

	private Logger logger = LoggerFactory.getLogger(JobStateService.class);

	@Autowired
	private JobStateRepository jobStateRepository;

	@Autowired
	private JobStateDtoMapper jobStateDtoMapper;

	@Value("${jobstatemanager.domain.jobflow.JobFlowService.maxBatchSize}")
	private int maxBatchSize;

	@Value("${jobstatemanager.domain.jobflow.JobFlowService.maxRetriesForError}")
	private int maxRetriesForError;

	private PageRequest pageRequest;

	@PostConstruct
	private void postConstruct() {
		pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "jobCreatedTimestamp"));
	}

//	@Transactional(transactionManager = "jobStateManagerTransactionManager")
	public List<JobStateDto> getJobStates() {

//		try {
//		jobStateRepository.findAll();
		return jobStateDtoMapper.toDto(jobStateRepository.findAll());
//		} catch (Throwable e) {
//			return null;
//		}
	}

//	@Transactional(transactionManager = "jobStateManagerTransactionManager")
	public List<JobStateDto> getJobStates(String appId) {

		try {
			return jobStateDtoMapper.toDto(jobStateRepository.findByAppId(appId));
		} catch (Exception e) {
			return null;
		}
	}

//	@Transactional(transactionManager = "jobStateManagerTransactionManager")
	public JobStateDto getJobState(String appId, String jobId) {

		JobState jobState = jobStateRepository.findByAppIdAndJobId(appId, jobId);
		if (jobState == null) {
			throw new EntityNotFoundException(String.format("Can't find JobFlow for appId=%s, jobId=%s", appId, jobId));
		}
		return jobStateDtoMapper.toDto(jobState);

	}

	/**
	 * Return next batch of job states
	 * 
	 * @return
	 */
//	@Transactional(transactionManager = "jobStateManagerTransactionManager")
	public List<JobStateDto> getNextBatch(String appId) {

		try {
			List<JobState> jobStates = jobStateRepository.findByAppIdAndStatusInAndErrorCountLessThan(appId,
					Arrays.asList(new JobStatus[] { JobStatus.NEW, JobStatus.ERROR }), maxRetriesForError + 1, pageRequest);
			logger.info("getNextBatch: number of submissions found: {}", jobStates.size());

			// mark each submission as picked up for processing
			jobStates.forEach(p -> {
				p.setStatus(JobStatus.PROCESSING);
				p.setStartTimestamp(new Timestamp(System.currentTimeMillis()));
				jobStateRepository.save(p);
			});
			return jobStateDtoMapper.toDto(jobStates);

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
//	@Transactional(transactionManager = "jobStateManagerTransactionManager")
	public List<String> getJobIds(String appId, boolean nextBatch) {

		List<JobStateDto> jobStateDtos = nextBatch ? getNextBatch(appId) : getJobStates(appId);
		return jobStateDtos.stream().map(p -> p.getJobId()).collect(Collectors.toList());

	}

	/**
	 * Return job flows for appId
	 * 
	 * @return
	 */
//	@Transactional(transactionManager = "jobStateManagerTransactionManager")
	public List<JobStateDto> getJobStates(String appId, boolean nextBatch) {

		return nextBatch ? getNextBatch(appId) : getJobStates(appId);
	}

//	@Transactional("jobStateManagerTransactionManager")
	public JobStateDto createJobState(JobStateDto jobStateDto) {

		JobState jobState = jobStateDtoMapper.fromDto(jobStateDto);
		jobState.setStatus(JobStatus.NEW);
		jobStateRepository.save(jobState);
		return jobStateDtoMapper.toDto(jobState);
	}

//	@Transactional("jobStateManagerTransactionManager")
	public JobStateDto updateJobState(String appId, String jobId, JobStateDto jobStateDto) {

		if (!jobStateRepository.existsByAppIdAndJobId(appId, jobId)) {
			throw new EntityNotFoundException("Can't find JobFlow: " + appId + ", " + jobId);
		}

		JobState jobState = jobStateRepository.getByAppIdAndJobId(appId, jobId);
		jobStateDtoMapper.fromDto(jobStateDto, jobState);

		jobStateRepository.save(jobState);
		return jobStateDtoMapper.toDto(jobState);
	}

//	@Transactional("jobStateManagerTransactionManager")
	public List<JobStateDto> patchJobStates(String appId, List<JobStateDto> jobStateDtos) {

		List<JobStateDto> returnJobFlowDtos = new ArrayList<>();
		List<String> jobIds = jobStateDtos.stream().map(p -> p.getJobId()).collect(Collectors.toList());
		List<JobState> jobStates = jobStateRepository.getByAppIdAndJobIdIn(appId, jobIds);

//		Map<String, String> jobIdStatusMap = jobStateDtos.stream()
//				.collect(Collectors.toMap(JobFlowDto::getJobId, JobFlowDto::getStatus));
		Map<String, JobState> jobIdJobFlowMap = jobStates.stream().collect(Collectors.toMap(JobState::getJobId, Function.identity()));

		jobStateDtos.forEach(p -> {
			JobState jobState = jobIdJobFlowMap.get(p.getJobId());
			jobStateDtoMapper.fromDtoPatch(p, jobState);
			jobStateRepository.save(jobState);
			JobStateDto jobStateDto = jobStateDtoMapper.toDto(jobState);
			returnJobFlowDtos.add(jobStateDto);
		});

		return returnJobFlowDtos;
	}

//	@Transactional(transactionManager = "jobStateManagerTransactionManager")
	public String deleteJobState(String appId, String jobId) {

		JobState jobState = jobStateRepository.findByAppIdAndJobId(appId, jobId);
		if (jobState == null) {
			throw new EntityNotFoundException(String.format("Can't find JobFlow for appId=%s, jobId=%s", appId, jobId));
		}

		return jobStateRepository.deleteByAppIdAndJobId(appId, jobId);
	}

}
