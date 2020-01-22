package gov.nyc.doitt.jobstatemanager.job;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nyc.doitt.jobstatemanager.AppConfig;
import gov.nyc.doitt.jobstatemanager.jobappconfig.JobAppConfig;
import gov.nyc.doitt.jobstatemanager.jobappconfig.JobAppConfigMockerUpper;
import gov.nyc.doitt.jobstatemanager.jobappconfig.JobAppConfigService;

@ContextConfiguration(classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class JobControllerTest {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private ApplicationContext applicationContext;

	@Mock
	private JobAppConfigService jobAppConfigService;

	@Autowired
	private JobDtoMockerUpper jobDtoMockerUpper;

	@Autowired
	private JobMockerUpper jobMockerUpper;

	@Autowired
	private JobAppConfigMockerUpper jobAppConfigMockerUpper;

	@Autowired
	@InjectMocks
	private JobService jobService;

	// mocking/unmocking of this bean is done explicitly below
	private JobRepository jobRepository;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		jobRepository = mock(JobRepository.class);
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {

		// put back real TaskRepository bean into TaskService
		jobRepository = applicationContext.getBean(JobRepository.class);
		ReflectionTestUtils.setField(jobService, "jobRepository", jobRepository);
	}

	@Test
	public void testCreate() throws Exception {

		JobDto jobDto = jobDtoMockerUpper.create();

		when(jobAppConfigService.existsJobAppConfig(jobDto.getAppId())).thenReturn(true);

		mockMvc.perform(post("/jobStateManager/jobs").contentType(MediaType.APPLICATION_JSON).content(asJsonString(jobDto)))
				.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.appId", comparesEqualTo(jobDto.getAppId())))
				.andExpect(jsonPath("$.jobId", comparesEqualTo(jobDto.getJobId())))
				.andExpect(jsonPath("$.state", comparesEqualTo(JobState.NEW.name())));

		verify(jobRepository).save(any(Job.class));
	}

	@Test
	public void testGet() throws Exception {

		List<Job> jobs = jobMockerUpper.createList(5);
		Job job0 = jobs.get(0);
		String appId = job0.getAppId();

		when(jobRepository.findByAppId(eq(appId), any(Sort.class))).thenReturn(jobs);

		ResultActions resultActions = mockMvc.perform(get("/jobStateManager/jobs/" + appId)).andDo(print())
				.andExpect(status().isOk());

		String content = resultActions.andReturn().getResponse().getContentAsString();
		List<JobDto> jobDtos = jobDtosJsonAsObject(content);

		assertEquals(jobs.size(), jobDtos.size());
		for (int i = 0; i < jobs.size(); i++) {
			Job job = jobs.get(i);
			JobDto jobDto = jobDtos.get(i);

			assertEquals(job.getJobId(), jobDto.getJobId());
		}

		verify(jobRepository).findByAppId(eq(appId), any(Sort.class));
	}

	@Test
	public void testStartNextBatch() throws Exception {

		List<Job> jobs = jobMockerUpper.createList(5);
		Job job0 = jobs.get(0);
		String appId = job0.getAppId();
		
		JobAppConfig jobAppConfig = jobAppConfigMockerUpper.create(appId);

		when(jobAppConfigService.existsJobAppConfig(job0.getAppId())).thenReturn(true);

		when(jobAppConfigService.getJobAppConfigDomain(job0.getAppId())).thenReturn(jobAppConfig);

		when(jobRepository.findByAppIdAndStateInAndErrorCountLessThan(eq(appId), anyList(), eq(jobAppConfig.getMaxRetriesForError() + 1), any(Pageable.class)))
				.thenReturn(jobs.subList(0, jobAppConfig.getMaxBatchSize()));

		ResultActions resultActions = mockMvc.perform(post("/jobStateManager/jobs/" + appId + "/startNextBatch")).andDo(print())
				.andExpect(status().isOk());

		String content = resultActions.andReturn().getResponse().getContentAsString();
		List<JobDto> jobDtos = jobDtosJsonAsObject(content);

		assertEquals(jobAppConfig.getMaxBatchSize(), jobDtos.size());
		for (int i = 0; i < jobDtos.size(); i++) {
			Job job = jobs.get(i);
			JobDto jobDto = jobDtos.get(i);

			assertEquals(job.getJobId(), jobDto.getJobId());
		}

		verify(jobRepository).findByAppIdAndStateInAndErrorCountLessThan(eq(appId), anyList(), eq(jobAppConfig.getMaxRetriesForError() + 1),
				any(Pageable.class));
	}

	@Test
	public void testUpdate() throws Exception {

		JobDto jobDto = jobDtoMockerUpper.create();
		Job job = jobMockerUpper.create();

		when(jobRepository.existsByAppIdAndJobId(eq(jobDto.getAppId()), eq(jobDto.getJobId()))).thenReturn(true);
		when(jobRepository.findByAppIdAndJobId(eq(jobDto.getAppId()), eq(jobDto.getJobId()))).thenReturn(job);

		mockMvc.perform(put("/jobStateManager/jobs/" + jobDto.getAppId() + "/job/" + jobDto.getJobId())
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(jobDto))).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.appId", comparesEqualTo(jobDto.getAppId())))
				.andExpect(jsonPath("$.jobId", comparesEqualTo(jobDto.getJobId())))
				.andExpect(jsonPath("$.state", comparesEqualTo(JobState.NEW.name())));

		verify(jobRepository).save(any(Job.class));
	}

	@Test
	public void testDelete() throws Exception {

		JobDto jobDto = jobDtoMockerUpper.create();

		when(jobRepository.existsByAppIdAndJobId(eq(jobDto.getAppId()), eq(jobDto.getJobId()))).thenReturn(true);

		mockMvc.perform(delete("/jobStateManager/jobs/" + jobDto.getAppId() + "/job/" + jobDto.getJobId())).andDo(print())
				.andExpect(status().isOk());

		verify(jobRepository).deleteByAppIdAndJobId(eq(jobDto.getAppId()), eq(jobDto.getJobId()));
	}

	private String asJsonString(Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List<JobDto> jobDtosJsonAsObject(String json) {
		try {
			return new ObjectMapper().readValue(json, new TypeReference<List<JobDto>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
