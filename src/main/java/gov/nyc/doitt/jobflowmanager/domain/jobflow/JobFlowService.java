package gov.nyc.doitt.jobflowmanager.domain.jobflow;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobFlow;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobStatus;

@Component
public class JobFlowService {

	private Logger logger = LoggerFactory.getLogger(JobFlowService.class);

	@Autowired
	private JobFlowRepository jobFlowRepository;

	@Value("${jobflowmanager.domain.jobflow.JobFlowService.maxBatchSize}")
	private int maxBatchSize;

	@Value("${jobflowmanager.domain.jobflow.JobFlowService.maxRetriesForError}")
	private int maxRetriesForError;

	private PageRequest pageRequest;

	@PostConstruct
	private void postConstruct() {
		pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "jobCreated"));
	}

	@Transactional(transactionManager = "jobFlowManagerTransactionManager")
	public List<JobFlow> getAll()  { 
		return jobFlowRepository.findAll();
	}

	/**
	 * Return next batch of submissions
	 * 
	 * @return
	 */
	@Transactional(transactionManager = "jobFlowManagerTransactionManager")
	public List<JobFlow> getNextBatch()  { 

		try {
			List<JobFlow> jobFlows = jobFlowRepository
					.findByStatusInAndErrorCountLessThan(
							Arrays.asList(new JobStatus[]{JobStatus.NEW, JobStatus.ERROR}),
							maxRetriesForError + 1, pageRequest);
			logger.info("getNextBatch: number of submissions found: {}", jobFlows.size());

			// mark each submission as picked up for processing
			jobFlows.forEach(p -> {
				p.setStatus(JobStatus.PROCESSING);
				p.setStartTimestamp(new Timestamp(System.currentTimeMillis()));
				updateJobFlow(p);
			});
			return jobFlows;

		} catch (Exception e) {
			throw new JobFlowManagerConcurrencyException(e);
		}

	}

	@Transactional("jobFlowManagerTransactionManager")
	public JobFlow createJobFlow(JobFlow jobFlow) {

		jobFlow.setStatus(JobStatus.NEW);
		jobFlowRepository.save(jobFlow);
		return jobFlow;
	}

	@Transactional("jobFlowManagerTransactionManager")
	public void updateJobFlow(JobFlow jobFlow) {

		jobFlowRepository.save(jobFlow);
	}

}
