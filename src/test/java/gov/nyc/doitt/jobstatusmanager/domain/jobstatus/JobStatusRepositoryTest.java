package gov.nyc.doitt.jobstatusmanager.domain.jobstatus;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import gov.nyc.doitt.jobstatusmanager.TestBase;
import gov.nyc.doitt.jobstatusmanager.domain.jobstatus.JobStatusRepository;
import gov.nyc.doitt.jobstatusmanager.domain.jobstatus.model.JobStatus;
import gov.nyc.doitt.jobstatusmanager.domain.jobstatus.model.JobStatusMockerUpper;
import gov.nyc.doitt.jobstatusmanager.domain.jobstatus.model.JobStatusType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JobStatusRepositoryTest extends TestBase {

	@Autowired
	private JobStatusRepository jobStatusRepository;

	@Autowired
	private JobStatusMockerUpper jobStatusMockerUpper;

	@Value("${jobstatusmanager.domain.JobStatusManagerService.maxBatchSize}")
	private int maxBatchSize;

	@Value("${jobstatusmanager.domain.JobStatusManagerService.maxRetriesForError}")
	private int maxRetriesForError;

	@Test
	@Transactional("jobStatusTransactionManager")
	public void testfindByStatusInAndErrorCountLessThan_NEW() throws Exception {

		maxBatchSize = 1;
		JobStatus jobStatus = jobStatusMockerUpper.create();
		jobStatus.setStatus(JobStatusType.NEW);
		jobStatusRepository.save(jobStatus);

		PageRequest pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "jobCreated"));
		List<JobStatus> jobStatuses = jobStatusRepository.findByStatusInAndErrorCountLessThan(
				Arrays.asList(new JobStatusType[] { JobStatusType.NEW }), maxRetriesForError, pageRequest);
		assertNotNull(jobStatuses);
		assertEquals(1, jobStatuses.size());
		assertTrue(jobStatuses.contains(jobStatus));
	}

	@Test
	@Transactional("jobStatusTransactionManager")
	public void testfindByStatusInAndErrorCountLessThan_LimitedByBatchSize() throws Exception {

		int numberOfJobStatuss = maxBatchSize + 5; // create more that are returned in batch
		List<JobStatus> jobStatuses = jobStatusMockerUpper.createList(numberOfJobStatuss);

		for (int i = 0; i < jobStatuses.size(); i++) {
			JobStatus jobStatus = jobStatuses.get(i);
			jobStatus.setStatus(i == 0 ? JobStatusType.NEW : JobStatusType.ERROR);
			jobStatusRepository.save(jobStatus);
		}

		PageRequest pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "jobCreated"));
		List<JobStatus> batchOfJobStatuss = jobStatusRepository.findByStatusInAndErrorCountLessThan(
				Arrays.asList(new JobStatusType[] { JobStatusType.NEW, JobStatusType.ERROR }), maxRetriesForError,
				pageRequest);
		assertNotNull(batchOfJobStatuss);
		assertEquals(maxBatchSize, batchOfJobStatuss.size());
		assertTrue(jobStatuses.containsAll(batchOfJobStatuss));
	}

	@Test
	@Transactional("jobStatusTransactionManager")
	public void testfindByStatusInAndErrorCountLessThan_NEW_and_ERROR_only() throws Exception {

		int numberOfJobStatuss = maxBatchSize + 20; // create more that are returned in batch
		List<JobStatus> jobStatuses = jobStatusMockerUpper.createList(numberOfJobStatuss);

		List<JobStatus> couldBeInBatchJobStatuss = new ArrayList<>();
		for (int i = 0; i < jobStatuses.size(); i++) {
			JobStatus jobStatus = jobStatuses.get(i);
			if (i % 11 == 0) {
				jobStatus.setStatus(null);
			} else if (i % 9 == 0) {
				jobStatus.setStatus(JobStatusType.PROCESSING);
			} else if (i % 7 == 0) {
				jobStatus.setStatus(JobStatusType.ERROR);
				jobStatus.setErrorCount(maxRetriesForError + 1);
			} else if (i % 3 == 0) {
				jobStatus.setStatus(JobStatusType.ERROR);
				couldBeInBatchJobStatuss.add(jobStatus);
			} else if (i % 2 == 0) {
				jobStatus.setStatus(JobStatusType.NEW);
				couldBeInBatchJobStatuss.add(jobStatus);
			}
			jobStatusRepository.save(jobStatus);
		}
		assertTrue(couldBeInBatchJobStatuss.size() >= maxBatchSize); // make sure our test data is valid

		PageRequest pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "jobCreated"));
		List<JobStatus> batchOfJobStatuss = jobStatusRepository.findByStatusInAndErrorCountLessThan(
				Arrays.asList(new JobStatusType[] { JobStatusType.NEW, JobStatusType.ERROR }), maxRetriesForError,
				pageRequest);
		assertNotNull(batchOfJobStatuss);
		assertEquals(maxBatchSize, batchOfJobStatuss.size());
		assertTrue(couldBeInBatchJobStatuss.containsAll(batchOfJobStatuss));
	}

}
