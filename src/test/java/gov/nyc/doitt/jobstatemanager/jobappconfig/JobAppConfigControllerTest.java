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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
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

@Configuration
@PropertySource("classpath:application.properties")
@ContextConfiguration(classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class JobAppConfigControllerTest {

	@Value("${server.servlet.context-path}")
	private String contextRoot;

	protected String getContextRoot() {
		return contextRoot;
	}

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

		when(jobAppConfigRepository.existsByAppName(jobAppConfigDto.getAppName())).thenReturn(false);

		mockMvc.perform(post(getContextRoot() + "/jobAppConfigs").contextPath(getContextRoot())
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(jobAppConfigDto))).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.appName", comparesEqualTo(jobAppConfigDto.getAppName())));

		verify(jobAppConfigRepository).save(any(JobAppConfig.class));
	}

	@Test
	public void testGetList() throws Exception {

		List<JobAppConfig> jobAppConfigs = jobAppConfigMockerUpper.createList(5);

		when(jobAppConfigRepository.findAllByOrderByAppNameAsc()).thenReturn(jobAppConfigs);

		ResultActions resultActions = mockMvc.perform(get("/jobStateManager/jobAppConfigs")).andDo(print())
				.andExpect(status().isOk());

		String content = resultActions.andReturn().getResponse().getContentAsString();
		List<JobAppConfigDto> jobAppConfigDtos = jobAppConfigDtosJsonAsObject(content);

		assertEquals(jobAppConfigs.size(), jobAppConfigDtos.size());
		for (int i = 0; i < jobAppConfigs.size(); i++) {
			JobAppConfig jobAppConfig = jobAppConfigs.get(i);
			JobAppConfigDto jobAppConfigDto = jobAppConfigDtos.get(i);

			assertEquals(jobAppConfig.getAppName(), jobAppConfigDto.getAppName());
		}

		verify(jobAppConfigRepository).findAllByOrderByAppNameAsc();
	}

	@Test
	public void testGet() throws Exception {

		JobAppConfig jobAppConfig = jobAppConfigMockerUpper.create("myApp1");
		String appName = jobAppConfig.getAppName();

		when(jobAppConfigRepository.existsByAppName(eq(appName))).thenReturn(true);
		when(jobAppConfigRepository.findByAppName(eq(appName))).thenReturn(jobAppConfig);

		ResultActions resultActions = mockMvc.perform(get("/jobStateManager/jobAppConfigs/" + appName)).andDo(print())
				.andExpect(status().isOk());

		String content = resultActions.andReturn().getResponse().getContentAsString();
		JobAppConfigDto jobAppConfigDto = jobAppConfigDtoJsonAsObject(content);
		assertEquals(jobAppConfig.getAppName(), jobAppConfigDto.getAppName());
		verify(jobAppConfigRepository).findByAppName(eq(appName));
	}

	@Test
	public void testUpdate() throws Exception {

		JobAppConfigDto jobAppConfigDto = jobAppConfigDtoMockerUpper.create();
		JobAppConfig jobAppConfig = jobAppConfigMockerUpper.create();

		when(jobAppConfigRepository.existsByAppName(eq(jobAppConfigDto.getAppName()))).thenReturn(true);
		when(jobAppConfigRepository.findByAppName(eq(jobAppConfigDto.getAppName()))).thenReturn(jobAppConfig);

		mockMvc.perform(put("/jobStateManager/jobAppConfigs/" + jobAppConfigDto.getAppName())
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(jobAppConfigDto))).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.appName", comparesEqualTo(jobAppConfigDto.getAppName())));

		verify(jobAppConfigRepository).save(any(JobAppConfig.class));
	}

	@Test
	public void testDelete() throws Exception {

		JobAppConfigDto jobAppConfigDto = jobAppConfigDtoMockerUpper.create();

		when(jobAppConfigRepository.existsByAppName(eq(jobAppConfigDto.getAppName()))).thenReturn(true);

		mockMvc.perform(delete("/jobStateManager/jobAppConfigs/" + jobAppConfigDto.getAppName())).andDo(print())
				.andExpect(status().isOk());

		verify(jobAppConfigRepository).deleteByAppName(eq(jobAppConfigDto.getAppName()));
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
