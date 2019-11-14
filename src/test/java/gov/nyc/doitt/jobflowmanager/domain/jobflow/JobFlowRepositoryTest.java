package gov.nyc.doitt.jobflowmanager.domain.jobflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import gov.nyc.doitt.jobflowmanager.TestBase;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobFlow;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobFlowMockerUpper;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobStatus;

@RunWith(SpringRunner.class)
public class JobFlowRepositoryTest extends TestBase {

	@Autowired
	private JobFlowRepository jobFlowRepository;

	@Autowired
	private JobFlowMockerUpper jobFlowMockerUpper;

	@Value("${jobflowmanager.domain.jobflow.JobFlowService.maxBatchSize}")
	private int maxBatchSize;

	@Value("${jobflowmanager.domain.jobflow.JobFlowService.maxRetriesForError}")
	private int maxRetriesForError;

	@Test
	@Transactional("jobFlowManagerTransactionManager")
	public void testfindByStatusInAndErrorCountLessThan_NEW() throws Exception {

		maxBatchSize = 1;
		JobFlow jobFlow = jobFlowMockerUpper.create();
		jobFlow.setStatus(JobStatus.NEW);
		jobFlowRepository.save(jobFlow);

		PageRequest pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "jobCreatedTimestamp"));
		List<JobFlow> jobFlows = jobFlowRepository.findByStatusInAndErrorCountLessThan(
				Arrays.asList(new JobStatus[] { JobStatus.NEW }), maxRetriesForError, pageRequest);
		assertNotNull(jobFlows);
		assertEquals(1, jobFlows.size());
		assertTrue(jobFlows.contains(jobFlow));
	}

	@Test
	@Transactional("jobFlowManagerTransactionManager")
	public void testfindByStatusInAndErrorCountLessThan_LimitedByBatchSize() throws Exception {

		int numberOfJobFlows = maxBatchSize + 5; // create more that are returned in batch
		List<JobFlow> jobFlows = jobFlowMockerUpper.createList(numberOfJobFlows);

		for (int i = 0; i < jobFlows.size(); i++) {
			JobFlow jobFlow = jobFlows.get(i);
			jobFlow.setStatus(i == 0 ? JobStatus.NEW : JobStatus.ERROR);
			jobFlowRepository.save(jobFlow);
		}

		PageRequest pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "jobCreatedTimestamp"));
		List<JobFlow> batchOfJobFlows = jobFlowRepository.findByStatusInAndErrorCountLessThan(
				Arrays.asList(new JobStatus[] { JobStatus.NEW, JobStatus.ERROR }), maxRetriesForError,
				pageRequest);
		assertNotNull(batchOfJobFlows);
		assertEquals(maxBatchSize, batchOfJobFlows.size());
		assertTrue(jobFlows.containsAll(batchOfJobFlows));
	}

	@Test
	@Transactional("jobFlowManagerTransactionManager")
	public void testfindByStatusInAndErrorCountLessThan_NEW_and_ERROR_only() throws Exception {

		int numberOfJobFlows = maxBatchSize + 20; // create more that are returned in batch
		List<JobFlow> jobFlows = jobFlowMockerUpper.createList(numberOfJobFlows);

		List<JobFlow> couldBeInBatchJobFlows = new ArrayList<>();
		for (int i = 0; i < jobFlows.size(); i++) {
			JobFlow jobFlow = jobFlows.get(i);
			if (i % 11 == 0) {
				jobFlow.setStatus(null);
			} else if (i % 9 == 0) {
				jobFlow.setStatus(JobStatus.PROCESSING);
			} else if (i % 7 == 0) {
				jobFlow.setStatus(JobStatus.ERROR);
				jobFlow.setErrorCount(maxRetriesForError + 1);
			} else if (i % 3 == 0) {
				jobFlow.setStatus(JobStatus.ERROR);
				couldBeInBatchJobFlows.add(jobFlow);
			} else if (i % 2 == 0) {
				jobFlow.setStatus(JobStatus.NEW);
				couldBeInBatchJobFlows.add(jobFlow);
			}
			jobFlowRepository.save(jobFlow);
		}
		assertTrue(couldBeInBatchJobFlows.size() >= maxBatchSize); // make sure our test data is valid

		PageRequest pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "jobCreatedTimestamp"));
		List<JobFlow> batchOfJobFlows = jobFlowRepository.findByStatusInAndErrorCountLessThan(
				Arrays.asList(new JobStatus[] { JobStatus.NEW, JobStatus.ERROR }), maxRetriesForError,
				pageRequest);
		assertNotNull(batchOfJobFlows);
		assertEquals(maxBatchSize, batchOfJobFlows.size());
		assertTrue(couldBeInBatchJobFlows.containsAll(batchOfJobFlows));
	}

}
