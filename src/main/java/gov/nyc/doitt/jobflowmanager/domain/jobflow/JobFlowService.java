package gov.nyc.doitt.jobflowmanager.domain.jobflow;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import gov.nyc.doitt.jobflowmanager.domain.jobflow.dto.JobFlowDto;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobFlow;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobStatus;
import gov.nyc.doitt.jobflowmanager.infrastructure.JobFlowManagerConcurrencyException;
import gov.nyc.doitt.jobflowmanager.infrastructure.JobFlowManagerException;

@Component
public class JobFlowService {

	private Logger logger = LoggerFactory.getLogger(JobFlowService.class);

	@Autowired
	private JobFlowRepository jobFlowRepository;

	@Autowired
	private JobFlowDtoMapper jobFlowDtoMapper;

	@Value("${jobflowmanager.domain.jobflow.JobFlowService.maxBatchSize}")
	private int maxBatchSize;

	@Value("${jobflowmanager.domain.jobflow.JobFlowService.maxRetriesForError}")
	private int maxRetriesForError;

	private PageRequest pageRequest;

	@PostConstruct
	private void postConstruct() {
		pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "jobCreatedTimestamp"));
	}

	@Transactional(transactionManager = "jobFlowManagerTransactionManager")
	public List<JobFlowDto> getJobFlows() {

		return jobFlowDtoMapper.toDto(jobFlowRepository.findAll());
	}

	@Transactional(transactionManager = "jobFlowManagerTransactionManager")
	public JobFlowDto getJobFlow(String appId, String jobId) {
		
		JobFlow jobFlow = jobFlowRepository.findByAppIdAndJobId(appId, jobId);
		if (jobFlow == null) {
			throw new EntityNotFoundException(String.format("Can't find JobFlow for appId=%s, jobId=%s", appId, jobId));
		}
		return jobFlowDtoMapper.toDto(jobFlow);
		
	}

	/**
	 * Return next batch of jobs
	 * 
	 * @return
	 */
	@Transactional(transactionManager = "jobFlowManagerTransactionManager")
	public List<JobFlowDto> getNextBatch(String appId) {

		try {
			List<JobFlow> jobFlows = jobFlowRepository.findByAppIdAndStatusInAndErrorCountLessThan(appId,
					Arrays.asList(new JobStatus[] { JobStatus.NEW, JobStatus.ERROR }), maxRetriesForError + 1, pageRequest);
			logger.info("getNextBatch: number of submissions found: {}", jobFlows.size());

			// mark each submission as picked up for processing
			jobFlows.forEach(p -> {
				p.setStatus(JobStatus.PROCESSING);
				p.setStartTimestamp(new Timestamp(System.currentTimeMillis()));
				jobFlowRepository.save(p);
			});
			return jobFlowDtoMapper.toDto(jobFlows);

		} catch (JobFlowManagerException e) {
			throw e;
		} catch (Exception e) {
			throw new JobFlowManagerConcurrencyException(e);
		}

	}

	@Transactional("jobFlowManagerTransactionManager")
	public JobFlowDto createJobFlow(JobFlowDto jobFlowDto) {

		JobFlow jobFlow = jobFlowDtoMapper.fromDto(jobFlowDto);
		jobFlow.setStatus(JobStatus.NEW);
		jobFlowRepository.save(jobFlow);
		return jobFlowDtoMapper.toDto(jobFlow);
	}

	@Transactional("jobFlowManagerTransactionManager")
	public JobFlowDto updateJobFlow(String appId, String jobId, JobFlowDto jobFlowDto) {

		if (!jobFlowRepository.existsByAppIdAndJobId(appId, jobId)) {
			throw new EntityNotFoundException("Can't find JobFlow: " + appId + ", " + jobId);
		}

		JobFlow jobFlow = jobFlowRepository.getByAppIdAndJobId(appId, jobId);
		jobFlowDtoMapper.fromDto(jobFlowDto, jobFlow);

		jobFlowRepository.save(jobFlow);
		return jobFlowDtoMapper.toDto(jobFlow);
	}

	@Transactional(transactionManager = "jobFlowManagerTransactionManager")
	public String deleteJobFlow(String appId, String jobId) {

		JobFlow jobFlow = jobFlowRepository.findByAppIdAndJobId(appId, jobId);
		if (jobFlow == null) {
			throw new EntityNotFoundException(String.format("Can't find JobFlow for appId=%s, jobId=%s", appId, jobId));
		}

		return jobFlowRepository.deleteByAppIdAndJobId(appId, jobId);
	}

}
