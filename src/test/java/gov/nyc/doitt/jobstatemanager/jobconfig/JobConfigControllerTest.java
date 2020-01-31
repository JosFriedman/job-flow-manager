package gov.nyc.doitt.jobstatemanager.jobconfig;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
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
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfigRepository;
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfigService;
import gov.nyc.doitt.jobstatemanager.test.BaseTest;

@RunWith(SpringJUnit4ClassRunner.class)
public class JobConfigControllerTest extends BaseTest {

	@Autowired
	private JobConfigDtoMockerUpper jobConfigDtoMockerUpper;

	@Autowired
	private JobConfigMockerUpper jobConfigMockerUpper;

	@Autowired
	@InjectMocks
	private JobConfigService jobConfigService;

	// mocking/unmocking of this bean is done explicitly below
	private JobConfigRepository jobConfigRepository;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();

		jobConfigRepository = mock(JobConfigRepository.class);
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {

		// put back real JobConfigRepository bean into JobConfigService
		jobConfigRepository = getApplicationContext().getBean(JobConfigRepository.class);
		ReflectionTestUtils.setField(jobConfigService, "jobConfigRepository", jobConfigRepository);
	}

	@Test
	public void testCreate() throws Exception {

		JobConfigDto jobConfigDto = jobConfigDtoMockerUpper.create();

		when(jobConfigRepository.existsByJobName(jobConfigDto.getJobName())).thenReturn(false);

		mockMvc.perform(post(getContextRoot() + "/jobConfigs").contextPath(getContextRoot())
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(jobConfigDto))).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.jobName", comparesEqualTo(jobConfigDto.getJobName())));

		verify(jobConfigRepository).save(any(JobConfig.class));
	}

	@Test
	public void testGetList() throws Exception {

		List<JobConfig> jobConfigs = jobConfigMockerUpper.createList(5);

		when(jobConfigRepository.findAllByOrderByJobNameAsc()).thenReturn(jobConfigs);

		ResultActions resultActions = mockMvc.perform(get(getContextRoot() + "/jobConfigs").contextPath(getContextRoot()))
				.andDo(print()).andExpect(status().isOk());

		String content = resultActions.andReturn().getResponse().getContentAsString();
		List<JobConfigDto> jobConfigDtos = jobConfigDtosJsonAsObject(content);

		assertEquals(jobConfigs.size(), jobConfigDtos.size());
		for (int i = 0; i < jobConfigs.size(); i++) {
			JobConfig jobConfig = jobConfigs.get(i);
			JobConfigDto jobConfigDto = jobConfigDtos.get(i);

			assertEquals(jobConfig.getJobName(), jobConfigDto.getJobName());
		}

		verify(jobConfigRepository).findAllByOrderByJobNameAsc();
	}

	@Test
	public void testGet() throws Exception {

		JobConfig jobConfig = jobConfigMockerUpper.create("myJob1");
		String jobName = jobConfig.getJobName();

		when(jobConfigRepository.existsByJobName(eq(jobName))).thenReturn(true);
		when(jobConfigRepository.findByJobName(eq(jobName))).thenReturn(jobConfig);

		ResultActions resultActions = mockMvc
				.perform(get(getContextRoot() + "/jobConfigs/" + jobName).contextPath(getContextRoot())).andDo(print())
				.andExpect(status().isOk());

		String content = resultActions.andReturn().getResponse().getContentAsString();
		JobConfigDto jobConfigDto = jobConfigDtoJsonAsObject(content);
		assertEquals(jobConfig.getJobName(), jobConfigDto.getJobName());
		verify(jobConfigRepository).findByJobName(eq(jobName));
	}

	@Test
	public void testUpdate() throws Exception {

		JobConfigDto jobConfigDto = jobConfigDtoMockerUpper.create();
		JobConfig jobConfig = jobConfigMockerUpper.create();

		when(jobConfigRepository.existsByJobName(eq(jobConfigDto.getJobName()))).thenReturn(true);
		when(jobConfigRepository.findByJobName(eq(jobConfigDto.getJobName()))).thenReturn(jobConfig);

		mockMvc.perform(put(getContextRoot() + "/jobConfigs/" + jobConfigDto.getJobName()).contextPath(getContextRoot())
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(jobConfigDto))).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.jobName", comparesEqualTo(jobConfigDto.getJobName())));

		verify(jobConfigRepository).save(any(JobConfig.class));
	}

	@Test
	public void testDelete() throws Exception {

		JobConfigDto jobConfigDto = jobConfigDtoMockerUpper.create();

		when(jobConfigRepository.existsByJobName(eq(jobConfigDto.getJobName()))).thenReturn(true);

		mockMvc.perform(delete(getContextRoot() + "/jobConfigs/" + jobConfigDto.getJobName()).contextPath(getContextRoot())).andDo(print())
				.andExpect(status().isOk());

		verify(jobConfigRepository).deleteByJobName(eq(jobConfigDto.getJobName()));
	}

	private List<JobConfigDto> jobConfigDtosJsonAsObject(String json) {
		try {
			return new ObjectMapper().readValue(json, new TypeReference<List<JobConfigDto>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private JobConfigDto jobConfigDtoJsonAsObject(String json) {
		try {
			return new ObjectMapper().readValue(json, new TypeReference<JobConfigDto>() {
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
