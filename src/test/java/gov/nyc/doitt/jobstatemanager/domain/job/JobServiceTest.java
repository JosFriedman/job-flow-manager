package gov.nyc.doitt.jobstatemanager.domain.job;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import gov.nyc.doitt.jobstatemanager.TestBase;
import gov.nyc.doitt.jobstatemanager.domain.job.JobDtoMapper;
import gov.nyc.doitt.jobstatemanager.domain.job.JobRepository;
import gov.nyc.doitt.jobstatemanager.domain.job.JobService;
import gov.nyc.doitt.jobstatemanager.domain.job.dto.JobDto;
import gov.nyc.doitt.jobstatemanager.domain.job.model.Job;
import gov.nyc.doitt.jobstatemanager.domain.job.model.JobMockerUpper;
import gov.nyc.doitt.jobstatemanager.domain.job.model.JobState;

@RunWith(SpringRunner.class)
public class JobServiceTest extends TestBase {

	@Autowired
	private JobMockerUpper JobMockerUpper;

	@Mock
	private JobRepository jobRepository;

	@Autowired
	private JobDtoMapper jobDtoMapper;

	@Spy
	@InjectMocks
	private JobService jobService = new JobService();

	@Value("${jobstatemanager.domain.job.JobService.maxBatchSize}")
	private int maxBatchSize;

	@Value("${jobstatemanager.domain.job.JobService.maxRetriesForError}")
	private int maxRetriesForError;

	private Pageable pageable;

	@Before
	public void init() throws Exception {

		pageable = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "createdTimestamp"));
		FieldUtils.writeField(jobService, "pageRequest", pageable, true);
		FieldUtils.writeField(jobService, "maxRetriesForError", maxRetriesForError, true);
		FieldUtils.writeField(jobService, "jobDtoMapper", jobDtoMapper, true);
	}

	@Test
	public void testJobServiceNoJobs() {

		String appId = "myApp";

		List<Job> jobs = Collections.emptyList();
		when(jobRepository.findByAppIdAndStateInAndErrorCountLessThan(eq(appId), ArgumentMatchers.<JobState>anyList(),
				eq(maxRetriesForError), eq(pageable))).thenReturn(jobs);

		List<JobDto> batchOfJobDtos = jobService.getNextBatch(appId);

		verify(jobRepository, times(1)).findByAppIdAndStateInAndErrorCountLessThan(eq(appId),
				ArgumentMatchers.<JobState>anyList(), anyInt(), any(Pageable.class));
		assertTrue(batchOfJobDtos.isEmpty());
	}

	@Test
	public void testJobServiceWithJobs() throws Exception {

		String appId = "myApp";

		int listSize = 5;
		List<Job> jobs = JobMockerUpper.createList(listSize);
		when(jobRepository.findByAppIdAndStateInAndErrorCountLessThan(eq(appId), ArgumentMatchers.<JobState>anyList(),
				anyInt(), any(Pageable.class))).thenReturn(jobs);

		when(jobRepository.existsByAppIdAndJobId(eq(appId), anyString())).thenReturn(true);

		List<JobDto> batchOfJobDtos = jobService.getNextBatch(appId);

		verify(jobRepository, times(1)).findByAppIdAndStateInAndErrorCountLessThan(eq(appId),
				ArgumentMatchers.<JobState>anyList(), anyInt(), any(Pageable.class));
		assertEquals(listSize, batchOfJobDtos.size());

		batchOfJobDtos.forEach(p -> {
			assertEquals(JobState.PROCESSING.toString(), p.getState());
			assertNotNull(p.getStartTimestamp());
		});
		verify(jobRepository, times(listSize)).save(any(Job.class));
	}

}
