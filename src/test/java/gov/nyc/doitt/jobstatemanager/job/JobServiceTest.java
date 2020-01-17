package gov.nyc.doitt.jobstatemanager.job;

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
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import gov.nyc.doitt.jobstatemanager.TestBase;
import gov.nyc.doitt.jobstatemanager.jobappconfig.JobAppConfig;
import gov.nyc.doitt.jobstatemanager.jobappconfig.JobAppConfigMockerUpper;
import gov.nyc.doitt.jobstatemanager.jobappconfig.JobAppConfigService;

@RunWith(SpringRunner.class)
public class JobServiceTest extends TestBase {

	@Autowired
	private JobMockerUpper JobMockerUpper;

	@Mock
	private JobRepository jobRepository;

	@Mock
	private JobAppConfigService jobAppConfigService;

	@Autowired
	private JobAppConfigMockerUpper jobAppConfigMockerUpper;

	@Autowired
	private JobDtoMapper jobDtoMapper;

	@Spy
	@InjectMocks
	private JobService jobService = new JobService();

	private Pageable pageable;

	@Before
	public void init() throws Exception {

		FieldUtils.writeField(jobService, "jobDtoMapper", jobDtoMapper, true);
	}

	@Test
	public void testJobServiceNoJobs() throws Exception {

		String appId = "myApp";
		JobAppConfig jobAppConfig = jobAppConfigMockerUpper.create(appId);
		when(jobAppConfigService.existsJobAppConfig(eq(appId))).thenReturn(true);
		when(jobAppConfigService.getJobAppConfigDomain(eq(appId))).thenReturn(jobAppConfig);

		List<Job> jobs = Collections.emptyList();
		when(jobRepository.findByAppIdAndStateInAndErrorCountLessThan(eq(appId), ArgumentMatchers.<JobState>anyList(), anyInt(),
				eq(pageable))).thenReturn(jobs);

		List<JobDto> batchOfJobDtos = jobService.startNextBatch(appId);

		verify(jobRepository, times(1)).findByAppIdAndStateInAndErrorCountLessThan(eq(appId), ArgumentMatchers.<JobState>anyList(),
				anyInt(), any(Pageable.class));
		assertTrue(batchOfJobDtos.isEmpty());
	}

	@Test
	public void testJobServiceWithJobs() throws Exception {

		String appId = "myApp";
		JobAppConfig jobAppConfig = jobAppConfigMockerUpper.create(appId);
		when(jobAppConfigService.existsJobAppConfig(eq(appId))).thenReturn(true);
		when(jobAppConfigService.getJobAppConfigDomain(eq(appId))).thenReturn(jobAppConfig);

		int listSize = 5;
		List<Job> jobs = JobMockerUpper.createList(listSize);

		when(jobRepository.findByAppIdAndStateInAndErrorCountLessThan(eq(appId), ArgumentMatchers.<JobState>anyList(), anyInt(),
				any(Pageable.class))).thenReturn(jobs);

		when(jobRepository.existsByAppIdAndJobId(eq(appId), anyString())).thenReturn(true);

		List<JobDto> batchOfJobDtos = jobService.startNextBatch(appId);

		verify(jobRepository, times(1)).findByAppIdAndStateInAndErrorCountLessThan(eq(appId), ArgumentMatchers.<JobState>anyList(),
				anyInt(), any(Pageable.class));
		assertEquals(listSize, batchOfJobDtos.size());

		batchOfJobDtos.forEach(p -> {
			assertEquals(JobState.PROCESSING.toString(), p.getState());
			assertNotNull(p.getStartTimestamp());
		});
		verify(jobRepository, times(listSize)).save(any(Job.class));
	}

}
