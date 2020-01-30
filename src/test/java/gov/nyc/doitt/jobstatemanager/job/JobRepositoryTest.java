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

import gov.nyc.doitt.jobstatemanager.test.BaseTest;

@RunWith(SpringRunner.class)
public class JobRepositoryTest extends BaseTest {

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobMockerUpper jobMockerUpper;

	private int maxBatchSize = 3;

	@Test
	public void whenJobsReadyAndValidNextTask_thenJobsShouldBeFound() throws Exception {

		maxBatchSize = 1;
		List<Job> jobs = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Job job = jobMockerUpper.create(i);
			job.setState(JobState.READY);
			jobRepository.save(job);
			jobs.add(job);
		}

		try {
			int j = 1;
			PageRequest pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "createdTimestamp"));
			List<Job> returnedJobs = jobRepository.findByJobNameAndStateInAndNextTaskName(jobMockerUpper.jobName,
					Arrays.asList(new JobState[] { JobState.READY }), "nextTaskName" + j, pageRequest);
			assertNotNull(returnedJobs);
			assertEquals(1, returnedJobs.size());
			assertTrue(returnedJobs.contains(jobs.get(j)));
		} finally {
			jobs.forEach(p -> jobRepository.delete(p));
		}
	}

	@Test
	public void whenJobsReadyAndNotValidNextTask_thenJobsShouldNotBeFound() throws Exception {

		maxBatchSize = 1;
		List<Job> jobs = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Job job = jobMockerUpper.create(i);
			job.setState(JobState.READY);
			jobRepository.save(job);
			jobs.add(job);
		}

		try {
			int j = 1;
			PageRequest pageRequest = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "createdTimestamp"));
			List<Job> returnedJobs = jobRepository.findByJobNameAndStateInAndNextTaskName(jobMockerUpper.jobName,
					Arrays.asList(new JobState[] { JobState.READY }), "nextTaskNameBlahBlahBlah", pageRequest);
			assertNotNull(returnedJobs);
			assertEquals(0, returnedJobs.size());
		} finally {
			jobs.forEach(p -> jobRepository.delete(p));
		}
	}

}
