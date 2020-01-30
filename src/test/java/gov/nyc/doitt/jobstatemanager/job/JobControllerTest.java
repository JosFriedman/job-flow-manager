package gov.nyc.doitt.jobstatemanager.job;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfig;
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfigDto;
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfigMockerUpper;
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfigService;
import gov.nyc.doitt.jobstatemanager.test.BaseTest;

@RunWith(SpringJUnit4ClassRunner.class)
public class JobControllerTest extends BaseTest {

	@Mock
	private JobConfigService jobConfigService;

	@Autowired
	private JobDtoMockerUpper jobDtoMockerUpper;

	@Autowired
	private JobMockerUpper jobMockerUpper;

	@Autowired
	private JobConfigMockerUpper jobConfigMockerUpper;

	@Autowired
	@InjectMocks
	private JobService jobService;

	// mocking/unmocking of this bean is done explicitly below
	private JobRepository jobRepository;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();

		jobRepository = mock(JobRepository.class);
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {

		// put back real JobRepository bean into JobService
		jobRepository = getApplicationContext().getBean(JobRepository.class);
		ReflectionTestUtils.setField(jobService, "jobRepository", jobRepository);
	}

	@Test
	public void testCreate() throws Exception {

		JobDto jobDto = jobDtoMockerUpper.create(2);
		JobConfig jobConfig = jobConfigMockerUpper.create(jobDto.getJobName());
		

		when(jobConfigService.existsJobConfig(jobDto.getJobName())).thenReturn(true);

		when(jobConfigService.getJobConfigDomain(jobDto.getJobName())).thenReturn(jobConfig);

		mockMvc.perform(post(getContextRoot() + "/jobs/" + jobDto.getJobName()).contentType(MediaType.APPLICATION_JSON).contextPath(getContextRoot())
				.content(asJsonString(jobDto))).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.jobName", comparesEqualTo(jobDto.getJobName())))
				.andExpect(jsonPath("$.jobId", comparesEqualTo(jobDto.getJobId())))
				.andExpect(jsonPath("$.state", comparesEqualTo(JobState.READY.name())));

		verify(jobRepository).save(any(Job.class));
	}

//	@Test
//	public void testGet() throws Exception {
//
//		List<Job> jobs = jobMockerUpper.createList(5);
//		Job job0 = jobs.get(0);
//		String jobName = job0.getJobName();
//
//		when(jobRepository.findByJobName(eq(jobName), any(Sort.class))).thenReturn(jobs);
//
//		ResultActions resultActions = mockMvc.perform(get(getContextRoot() + "/jobs/" + jobName).contextPath(getContextRoot()))
//				.andDo(print()).andExpect(status().isOk());
//
//		String content = resultActions.andReturn().getResponse().getContentAsString();
//		List<JobDto> jobDtos = jobDtosJsonAsObject(content);
//
//		assertEquals(jobs.size(), jobDtos.size());
//		for (int i = 0; i < jobs.size(); i++) {
//			Job job = jobs.get(i);
//			JobDto jobDto = jobDtos.get(i);
//
//			assertEquals(job.getJobId(), jobDto.getJobId());
//		}
//
//		verify(jobRepository).findByJobName(eq(jobName), any(Sort.class));
//	}

//	@Test
//	public void testStartNextBatch() throws Exception {
//
//		List<Job> jobs = jobMockerUpper.createList(5);
//		Job job0 = jobs.get(0);
//		String jobName = job0.getJobName();
//		
//		JobConfig jobConfig = jobConfigMockerUpper.create(jobName);
//
//		when(jobConfigService.existsJobConfig(job0.getJobName())).thenReturn(true);
//
//		when(jobConfigService.getJobConfigDomain(job0.getJobName())).thenReturn(jobConfig);
//
//		when(jobRepository.findByJobNameAndStateInAndErrorCountLessThan(eq(jobName), anyList(), eq(jobConfig.getMaxRetriesForError() + 1), any(Pageable.class)))
//				.thenReturn(jobs.subList(0, jobConfig.getMaxBatchSize()));
//
//		ResultActions resultActions = mockMvc.perform(post("/jobStateManager/jobs/" + jobName + "/startNextBatch")).andDo(print())
//				.andExpect(status().isOk());
//
//		String content = resultActions.andReturn().getResponse().getContentAsString();
//		List<JobDto> jobDtos = jobDtosJsonAsObject(content);
//
//		assertEquals(jobConfig.getMaxBatchSize(), jobDtos.size());
//		for (int i = 0; i < jobDtos.size(); i++) {
//			Job job = jobs.get(i);
//			JobDto jobDto = jobDtos.get(i);
//
//			assertEquals(job.getJobId(), jobDto.getJobId());
//		}
//
//		verify(jobRepository).findByJobNameAndStateInAndErrorCountLessThan(eq(jobName), anyList(), eq(jobConfig.getMaxRetriesForError() + 1),
//				any(Pageable.class));
//	}
//
//	@Test
//	public void testUpdate() throws Exception {
//
//		JobDto jobDto = jobDtoMockerUpper.create();
//		Job job = jobMockerUpper.create();
//
//		when(jobRepository.existsByJobNameAndJobId(eq(jobDto.getJobName()), eq(jobDto.getJobId()))).thenReturn(true);
//		when(jobRepository.findByJobNameAndJobId(eq(jobDto.getJobName()), eq(jobDto.getJobId()))).thenReturn(job);
//
//		mockMvc.perform(put("/jobStateManager/jobs/" + jobDto.getJobName() + "/job/" + jobDto.getJobId())
//				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(jobDto))).andDo(print()).andExpect(status().isOk())
//				.andExpect(jsonPath("$.jobName", comparesEqualTo(jobDto.getJobName())))
//				.andExpect(jsonPath("$.jobId", comparesEqualTo(jobDto.getJobId())))
//				.andExpect(jsonPath("$.state", comparesEqualTo(JobState.READY.name())));
//
//		verify(jobRepository).save(any(Job.class));
//	}
//
//	@Test
//	public void testDelete() throws Exception {
//
//		JobDto jobDto = jobDtoMockerUpper.create();
//
//		when(jobRepository.existsByJobNameAndJobId(eq(jobDto.getJobName()), eq(jobDto.getJobId()))).thenReturn(true);
//
//		mockMvc.perform(delete("/jobStateManager/jobs/" + jobDto.getJobName() + "/job/" + jobDto.getJobId())).andDo(print())
//				.andExpect(status().isOk());
//
//		verify(jobRepository).deleteByJobNameAndJobId(eq(jobDto.getJobName()), eq(jobDto.getJobId()));
//	}
//
//	private String asJsonString(Object obj) {
//		try {
//			return new ObjectMapper().writeValueAsString(obj);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//
	private List<JobDto> jobDtosJsonAsObject(String json) {
		try {
			return new ObjectMapper().readValue(json, new TypeReference<List<JobDto>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
