package gov.nyc.doitt.jobstatemanager.domain.jobstate;

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

import gov.nyc.doitt.jobstatemanager.TestBase;
import gov.nyc.doitt.jobstatemanager.domain.jobstate.JobStateRepository;
import gov.nyc.doitt.jobstatemanager.domain.jobstate.model.JobState;
import gov.nyc.doitt.jobstatemanager.domain.jobstate.model.JobStateMockerUpper;
import gov.nyc.doitt.jobstatemanager.domain.jobstate.model.JobStatus;

@RunWith(SpringRunner.class)
public class JobStateRepositoryTest extends TestBase {

	@Autowired
	private JobStateRepository jobStateRepository;

	@Autowired
	private JobStateMockerUpper jobStateMockerUpper;

	@Value("${jobstatemanager.domain.jobflow.JobFlowService.maxBatchSize}")
	private int maxBatchSize;

	@Value("${jobstatemanager.domain.jobflow.JobFlowService.maxRetriesForError}")
	private int maxRetriesForError;

	@Test
//	@Transactional("jobStateManagerTransactionManager")
	public void testfindByStatusInAndErrorCountLessThan_NEW() throws Exception {

		maxBatchSize = 1;
		JobState jobState = jobStateMockerUpper.create();
		jobState.setStatus(JobStatus.NEW);
		jobStateRepository.save(jobState);

		try {
			PageRequest pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "jobCreatedTimestamp"));
			List<JobState> jobStates = jobStateRepository.findByAppIdAndStatusInAndErrorCountLessThan(jobStateMockerUpper.appId,
					Arrays.asList(new JobStatus[] { JobStatus.NEW }), maxRetriesForError, pageRequest);
			assertNotNull(jobStates);
			assertEquals(1, jobStates.size());
			assertTrue(jobStates.contains(jobState));
		} finally {
			jobStateRepository.delete(jobState);
		}
	}

	@Test
//	@Transactional("jobStateManagerTransactionManager")
	public void testfindByStatusInAndErrorCountLessThan_LimitedByBatchSize() throws Exception {

		int numberOfJobFlows = maxBatchSize + 5; // create more that are returned in batch
		List<JobState> jobStates = jobStateMockerUpper.createList(numberOfJobFlows);

		try {
			for (int i = 0; i < jobStates.size(); i++) {
				JobState jobState = jobStates.get(i);
				jobState.setStatus(i == 0 ? JobStatus.NEW : JobStatus.ERROR);
				jobStateRepository.save(jobState);
			}

			PageRequest pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "jobCreatedTimestamp"));
			List<JobState> batchOfJobFlows = jobStateRepository.findByAppIdAndStatusInAndErrorCountLessThan(jobStateMockerUpper.appId,
					Arrays.asList(new JobStatus[] { JobStatus.NEW, JobStatus.ERROR }), maxRetriesForError, pageRequest);
			assertNotNull(batchOfJobFlows);
			assertEquals(maxBatchSize, batchOfJobFlows.size());
			assertTrue(jobStates.containsAll(batchOfJobFlows));
		} finally {
			jobStates.forEach(p -> jobStateRepository.delete(p));
		}
	}

	@Test
//	@Transactional("jobStateManagerTransactionManager")
	public void testfindByStatusInAndErrorCountLessThan_NEW_and_ERROR_only() throws Exception {

		int numberOfJobFlows = maxBatchSize + 20; // create more that are returned in batch
		List<JobState> jobStates = jobStateMockerUpper.createList(numberOfJobFlows);

		try {
			List<JobState> couldBeInBatchJobFlows = new ArrayList<>();
			for (int i = 0; i < jobStates.size(); i++) {
				JobState jobState = jobStates.get(i);
				if (i % 11 == 0) {
					jobState.setStatus(null);
				} else if (i % 9 == 0) {
					jobState.setStatus(JobStatus.PROCESSING);
				} else if (i % 7 == 0) {
					jobState.setStatus(JobStatus.ERROR);
					jobState.setErrorCount(maxRetriesForError + 1);
				} else if (i % 3 == 0) {
					jobState.setStatus(JobStatus.ERROR);
					couldBeInBatchJobFlows.add(jobState);
				} else if (i % 2 == 0) {
					jobState.setStatus(JobStatus.NEW);
					couldBeInBatchJobFlows.add(jobState);
				}
				jobStateRepository.save(jobState);
			}
			assertTrue(couldBeInBatchJobFlows.size() >= maxBatchSize); // make sure our test data is valid

			PageRequest pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "jobCreatedTimestamp"));
			List<JobState> batchOfJobFlows = jobStateRepository.findByAppIdAndStatusInAndErrorCountLessThan(jobStateMockerUpper.appId,
					Arrays.asList(new JobStatus[] { JobStatus.NEW, JobStatus.ERROR }), maxRetriesForError, pageRequest);
			assertNotNull(batchOfJobFlows);
			assertEquals(maxBatchSize, batchOfJobFlows.size());
			assertTrue(couldBeInBatchJobFlows.containsAll(batchOfJobFlows));

		} finally {
			jobStates.forEach(p -> jobStateRepository.delete(p));
		}
	}

}
