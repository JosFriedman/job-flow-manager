package gov.nyc.doitt.jobstatemanager.jobappconfig;

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
import org.springframework.context.ApplicationContext;
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

@ContextConfiguration(classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class JobAppConfigControllerTest {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private JobAppConfigDtoMockerUpper jobAppConfigDtoMockerUpper;

	@Autowired
	private JobAppConfigMockerUpper jobAppConfigMockerUpper;

	@Autowired
	@InjectMocks
	private JobAppConfigService jobAppConfigService;

	// mocking/unmocking of this bean is done explicitly below
	private JobAppConfigRepository jobAppConfigRepository;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		jobAppConfigRepository = mock(JobAppConfigRepository.class);
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {

		// put back real JobAppConfigRepository bean into JobAppConfigService
		jobAppConfigRepository = applicationContext.getBean(JobAppConfigRepository.class);
		ReflectionTestUtils.setField(jobAppConfigService, "jobAppConfigRepository", jobAppConfigRepository);
	}

	@Test
	public void testCreate() throws Exception {

		JobAppConfigDto jobAppConfigDto = jobAppConfigDtoMockerUpper.create();

		when(jobAppConfigRepository.existsByAppId(jobAppConfigDto.getAppId())).thenReturn(false);

		mockMvc.perform(post("/jobStateManager/jobAppConfigs").contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(jobAppConfigDto))).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.appId", comparesEqualTo(jobAppConfigDto.getAppId())));

		verify(jobAppConfigRepository).save(any(JobAppConfig.class));
	}

	@Test
	public void testGetList() throws Exception {

		List<JobAppConfig> jobAppConfigs = jobAppConfigMockerUpper.createList(5);

		when(jobAppConfigRepository.findAllByOrderByAppIdAsc()).thenReturn(jobAppConfigs);

		ResultActions resultActions = mockMvc.perform(get("/jobStateManager/jobAppConfigs")).andDo(print())
				.andExpect(status().isOk());

		String content = resultActions.andReturn().getResponse().getContentAsString();
		List<JobAppConfigDto> jobAppConfigDtos = jobAppConfigDtosJsonAsObject(content);

		assertEquals(jobAppConfigs.size(), jobAppConfigDtos.size());
		for (int i = 0; i < jobAppConfigs.size(); i++) {
			JobAppConfig jobAppConfig = jobAppConfigs.get(i);
			JobAppConfigDto jobAppConfigDto = jobAppConfigDtos.get(i);

			assertEquals(jobAppConfig.getAppId(), jobAppConfigDto.getAppId());
		}

		verify(jobAppConfigRepository).findAllByOrderByAppIdAsc();
	}

	@Test
	public void testGet() throws Exception {

		JobAppConfig jobAppConfig = jobAppConfigMockerUpper.create("myApp1");
		String appId = jobAppConfig.getAppId();

		when(jobAppConfigRepository.existsByAppId(eq(appId))).thenReturn(true);
		when(jobAppConfigRepository.findByAppId(eq(appId))).thenReturn(jobAppConfig);

		ResultActions resultActions = mockMvc.perform(get("/jobStateManager/jobAppConfigs/" + appId)).andDo(print())
				.andExpect(status().isOk());

		String content = resultActions.andReturn().getResponse().getContentAsString();
		JobAppConfigDto jobAppConfigDto = jobAppConfigDtoJsonAsObject(content);
		assertEquals(jobAppConfig.getAppId(), jobAppConfigDto.getAppId());
		verify(jobAppConfigRepository).findByAppId(eq(appId));
	}

	@Test
	public void testUpdate() throws Exception {

		JobAppConfigDto jobAppConfigDto = jobAppConfigDtoMockerUpper.create();
		JobAppConfig jobAppConfig = jobAppConfigMockerUpper.create();

		when(jobAppConfigRepository.existsByAppId(eq(jobAppConfigDto.getAppId()))).thenReturn(true);
		when(jobAppConfigRepository.findByAppId(eq(jobAppConfigDto.getAppId()))).thenReturn(jobAppConfig);

		mockMvc.perform(put("/jobStateManager/jobAppConfigs/" + jobAppConfigDto.getAppId()).contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(jobAppConfigDto))).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.appId", comparesEqualTo(jobAppConfigDto.getAppId())));

		verify(jobAppConfigRepository).save(any(JobAppConfig.class));
	}

	@Test
	public void testDelete() throws Exception {

		JobAppConfigDto jobAppConfigDto = jobAppConfigDtoMockerUpper.create();

		when(jobAppConfigRepository.existsByAppId(eq(jobAppConfigDto.getAppId()))).thenReturn(true);

		mockMvc.perform(delete("/jobStateManager/jobAppConfigs/" + jobAppConfigDto.getAppId())).andDo(print())
				.andExpect(status().isOk());

		verify(jobAppConfigRepository).deleteByAppId(eq(jobAppConfigDto.getAppId()));
	}

	private String asJsonString(Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List<JobAppConfigDto> jobAppConfigDtosJsonAsObject(String json) {
		try {
			return new ObjectMapper().readValue(json, new TypeReference<List<JobAppConfigDto>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private JobAppConfigDto jobAppConfigDtoJsonAsObject(String json) {
		try {
			return new ObjectMapper().readValue(json, new TypeReference<JobAppConfigDto>() {
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
