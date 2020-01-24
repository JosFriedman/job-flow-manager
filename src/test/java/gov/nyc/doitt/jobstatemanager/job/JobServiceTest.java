package gov.nyc.doitt.jobstatemanager.job;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import gov.nyc.doitt.jobstatemanager.TestBase;
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
	public void dummy() {

	}
	
//	@Test
//	public void testJobServiceNoJobs() throws Exception {
//
//		String appName = "myApp";
//		JobAppConfig jobAppConfig = jobAppConfigMockerUpper.create(appName);
//		when(jobAppConfigService.existsJobAppConfig(eq(appName))).thenReturn(true);
//		when(jobAppConfigService.getJobAppConfigDomain(eq(appName))).thenReturn(jobAppConfig);
//
//		List<Job> jobs = Collections.emptyList();
//		when(jobRepository.findByAppNameAndStateInAndErrorCountLessThan(eq(appName), ArgumentMatchers.<TaskState>anyList(), anyInt(),
//				eq(pageable))).thenReturn(jobs);
//
//		List<JobDto> batchOfJobDtos = jobService.startNextBatch(appName);
//
//		verify(jobRepository, times(1)).findByAppNameAndStateInAndErrorCountLessThan(eq(appName), ArgumentMatchers.<TaskState>anyList(),
//				anyInt(), any(Pageable.class));
//		assertTrue(batchOfJobDtos.isEmpty());
//	}
//
//	@Test
//	public void testJobServiceWithJobs() throws Exception {
//
//		String appName = "myApp";
//		JobAppConfig jobAppConfig = jobAppConfigMockerUpper.create(appName);
//		when(jobAppConfigService.existsJobAppConfig(eq(appName))).thenReturn(true);
//		when(jobAppConfigService.getJobAppConfigDomain(eq(appName))).thenReturn(jobAppConfig);
//
//		int listSize = 5;
//		List<Job> jobs = JobMockerUpper.createList(listSize);
//
//		when(jobRepository.findByAppNameAndStateInAndErrorCountLessThan(eq(appName), ArgumentMatchers.<TaskState>anyList(), anyInt(),
//				any(Pageable.class))).thenReturn(jobs);
//
//		when(jobRepository.existsByAppNameAndJobId(eq(appName), anyString())).thenReturn(true);
//
//		List<JobDto> batchOfJobDtos = jobService.startNextBatch(appName);
//
//		verify(jobRepository, times(1)).findByAppNameAndStateInAndErrorCountLessThan(eq(appName), ArgumentMatchers.<TaskState>anyList(),
//				anyInt(), any(Pageable.class));
//		assertEquals(listSize, batchOfJobDtos.size());
//
//		batchOfJobDtos.forEach(p -> {
//			assertEquals(TaskState.PROCESSING.toString(), p.getState());
//			assertNotNull(p.getStartTimestamp());
//		});
//		verify(jobRepository, times(listSize)).save(any(Job.class));
//	}

}
