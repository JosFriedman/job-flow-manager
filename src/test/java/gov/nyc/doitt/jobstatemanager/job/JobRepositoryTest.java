package gov.nyc.doitt.jobstatemanager.job;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import gov.nyc.doitt.jobstatemanager.TestBase;

@RunWith(SpringRunner.class)
public class JobRepositoryTest extends TestBase {

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobMockerUpper jobMockerUpper;

	private int maxBatchSize = 3;

	private int maxRetriesForError = 2;

	@Test
	public void testfindByStatusInAndErrorCountLessThan_NEW() throws Exception {

		maxBatchSize = 1;
		Job job = jobMockerUpper.create();
		job.setState(JobState.READY);
		jobRepository.save(job);

		try {
			PageRequest pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "createdTimestamp"));
			List<Job> jobs = jobRepository.findByAppNameAndStateInAndErrorCountLessThan(jobMockerUpper.appName,
					Arrays.asList(new JobState[] { JobState.READY }), maxRetriesForError, pageRequest);
			assertNotNull(jobs);
			assertEquals(1, jobs.size());
			assertTrue(jobs.contains(job));
		} finally {
			jobRepository.delete(job);
		}
	}

	@Test
	public void testfindByStatusInAndErrorCountLessThan_LimitedByBatchSize() throws Exception {

		int numberOfJobs = maxBatchSize + 5; // create more that are returned in batch
		List<Job> jobs = jobMockerUpper.createList(numberOfJobs);

		try {
			for (int i = 0; i < jobs.size(); i++) {
				Job job = jobs.get(i);
				job.setState(i == 0 ? JobState.READY : JobState.ERROR);
				jobRepository.save(job);
			}

			PageRequest pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "createdTimestamp"));
			List<Job> batchOfJobs = jobRepository.findByAppNameAndStateInAndErrorCountLessThan(jobMockerUpper.appName,
					Arrays.asList(new JobState[] { JobState.READY, JobState.ERROR }), maxRetriesForError, pageRequest);
			assertNotNull(batchOfJobs);
			assertEquals(maxBatchSize, batchOfJobs.size());
			assertTrue(jobs.containsAll(batchOfJobs));
		} finally {
			jobs.forEach(p -> jobRepository.delete(p));
		}
	}

	@Test
	public void testfindByStatusInAndErrorCountLessThan_NEW_and_ERROR_only() throws Exception {

		int numberOfJobs = maxBatchSize + 20; // create more that are returned in batch
		List<Job> jobs = jobMockerUpper.createList(numberOfJobs);

		try {
			List<Job> couldBeInBatchJobs = new ArrayList<>();
			for (int i = 0; i < jobs.size(); i++) {
				Job job = jobs.get(i);
				if (i % 9 == 0) {
					job.setState(JobState.PROCESSING);
				} else if (i % 7 == 0) {
					job.setState(JobState.ERROR);
					job.setErrorCount(maxRetriesForError + 1);
				} else if (i % 3 == 0) {
					job.setState(JobState.ERROR);
					couldBeInBatchJobs.add(job);
				} else if (i % 2 == 0) {
					job.setState(JobState.READY);
					couldBeInBatchJobs.add(job);
				} else {
					job.setState(JobState.PROCESSING);
				}
				jobRepository.save(job);
			}
			assertTrue(couldBeInBatchJobs.size() >= maxBatchSize); // make sure our test data is valid

			PageRequest pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "createdTimestamp"));
			List<Job> batchOfJobs = jobRepository.findByAppNameAndStateInAndErrorCountLessThan(jobMockerUpper.appName,
					Arrays.asList(new JobState[] { JobState.READY, JobState.ERROR }), maxRetriesForError + 1, pageRequest);
			assertNotNull(batchOfJobs);
			assertEquals(maxBatchSize, batchOfJobs.size());
			assertTrue(couldBeInBatchJobs.containsAll(batchOfJobs));

		} finally {
			jobs.forEach(p -> jobRepository.delete(p));
		}
	}

}
