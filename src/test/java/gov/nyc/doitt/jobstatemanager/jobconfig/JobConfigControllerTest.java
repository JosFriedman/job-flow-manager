package gov.nyc.doitt.jobstatemanager.jobconfig;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nyc.doitt.jobstatemanager.security.JobAuthorizer;
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

	@Autowired
	@InjectMocks
	private JobAuthorizer jobAuthorizer;

	// mocking/unmocking of this bean is done explicitly below
	private JobConfigRepository jobConfigRepository;

	private MockMvc mockMvc;

	private HttpHeaders httpHeaders;

	private static final String NON_ADMIN_AUTH_TOKEN = "RV9do3MRUY3gw1aclo-J#cAi6xQCJzqE-B9#LhCL)U+)jE%`eMek)4m9FSuG~y+w";

	private static final String ADMIN_AUTH_TOKEN = "G0Ts6!yeH^uJuLIaa`J2=W#+t~p-faEgw=~Fyp0qXY778IyJAUs+^PU)=OZBiayn";

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).apply(springSecurity()).build();

		jobConfigRepository = mock(JobConfigRepository.class);
		MockitoAnnotations.initMocks(this);

		httpHeaders = new HttpHeaders();
	}

	@After
	public void tearDown() throws Exception {

		// put back real JobConfigRepository bean into JobConfigService
		jobConfigRepository = getApplicationContext().getBean(JobConfigRepository.class);
		ReflectionTestUtils.setField(jobConfigService, "jobConfigRepository", jobConfigRepository);
	}

	@Test
	public void testCreate_succeedAdmin() throws Exception {

		httpHeaders.add("Authorization", "Bearer " + ADMIN_AUTH_TOKEN);

		JobConfigDto jobConfigDto = jobConfigDtoMockerUpper.create();

		when(jobConfigRepository.existsByJobName(jobConfigDto.getJobName())).thenReturn(false);

		mockMvc.perform(post(getContextRoot() + "/jobConfigs").headers(httpHeaders).contextPath(getContextRoot())
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(jobConfigDto))).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.jobName", comparesEqualTo(jobConfigDto.getJobName())));

		verify(jobConfigRepository).save(any(JobConfig.class));
	}

	@Test
	public void testCreate_failNoAdmin() throws Exception {

		httpHeaders.add("Authorization", "Bearer " + NON_ADMIN_AUTH_TOKEN);

		JobConfigDto jobConfigDto = jobConfigDtoMockerUpper.create();

		when(jobConfigRepository.existsByJobName(jobConfigDto.getJobName())).thenReturn(false);

		ResultActions resultActions = mockMvc
				.perform(post(getContextRoot() + "/jobConfigs").headers(httpHeaders).contextPath(getContextRoot())
						.contentType(MediaType.APPLICATION_JSON).content(asJsonString(jobConfigDto)))
				.andDo(print()).andExpect(status().isForbidden()).andExpect(jsonPath("$.errors", not(IsEmptyCollection.empty())));

		String s = resultActions.andReturn().getResponse().getContentAsString();
		assertEquals("{\"errors\":{\"accessDenied\":\"Access is denied\"}}", s);

		verify(jobConfigRepository, times(0)).save(any(JobConfig.class));
	}

	@Test
	public void testGetAll_succeedAdmin() throws Exception {

		httpHeaders.add("Authorization", "Bearer " + ADMIN_AUTH_TOKEN);

		List<JobConfig> jobConfigs = jobConfigMockerUpper.createList(5);

		when(jobConfigRepository.findAllByOrderByJobNameAsc()).thenReturn(jobConfigs);

		ResultActions resultActions = mockMvc
				.perform(get(getContextRoot() + "/jobConfigs").headers(httpHeaders).contextPath(getContextRoot())).andDo(print())
				.andExpect(status().isOk());

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
	public void testGetAll_failNoAdmin() throws Exception {

		httpHeaders.add("Authorization", "Bearer " + NON_ADMIN_AUTH_TOKEN);

		List<JobConfig> jobConfigs = jobConfigMockerUpper.createList(5);

		when(jobConfigRepository.findAllByOrderByJobNameAsc()).thenReturn(jobConfigs);

		ResultActions resultActions = mockMvc
				.perform(get(getContextRoot() + "/jobConfigs").headers(httpHeaders).contextPath(getContextRoot())).andDo(print())
				.andExpect(status().isForbidden()).andExpect(jsonPath("$.errors", not(IsEmptyCollection.empty())));

		String s = resultActions.andReturn().getResponse().getContentAsString();
		assertEquals("{\"errors\":{\"accessDenied\":\"Access is denied\"}}", s);

		verify(jobConfigRepository, times(0)).findAllByOrderByJobNameAsc();
	}

	@Test
	public void testGetByJobName_succeedAdmin() throws Exception {

		httpHeaders.add("Authorization", "Bearer " + ADMIN_AUTH_TOKEN);

		JobConfig jobConfig = jobConfigMockerUpper.create("myJob1");
		String jobName = jobConfig.getJobName();

		when(jobConfigRepository.existsByJobName(eq(jobName))).thenReturn(true);
		when(jobConfigRepository.findByJobName(eq(jobName))).thenReturn(jobConfig);

		ResultActions resultActions = mockMvc
				.perform(get(getContextRoot() + "/jobConfigs/" + jobName).headers(httpHeaders).contextPath(getContextRoot()))
				.andDo(print()).andExpect(status().isOk());

		String content = resultActions.andReturn().getResponse().getContentAsString();
		JobConfigDto jobConfigDto = jobConfigDtoJsonAsObject(content);
		assertEquals(jobConfig.getJobName(), jobConfigDto.getJobName());
		verify(jobConfigRepository).findByJobName(eq(jobName));
	}

	@Test
	public void testGetByJobName_failNoAdmin() throws Exception {

		httpHeaders.add("Authorization", "Bearer " + NON_ADMIN_AUTH_TOKEN);

		JobConfig jobConfig = jobConfigMockerUpper.create("myJob1");
		String jobName = jobConfig.getJobName();

		when(jobConfigRepository.existsByJobName(eq(jobName))).thenReturn(true);
		when(jobConfigRepository.findByJobName(eq(jobName))).thenReturn(jobConfig);

		ResultActions resultActions = mockMvc
				.perform(get(getContextRoot() + "/jobConfigs/" + jobName).headers(httpHeaders).contextPath(getContextRoot()))
				.andDo(print()).andExpect(status().isForbidden()).andExpect(jsonPath("$.errors", not(IsEmptyCollection.empty())));

		String s = resultActions.andReturn().getResponse().getContentAsString();
		assertEquals("{\"errors\":{\"accessDenied\":\"Access is denied\"}}", s);

		verify(jobConfigRepository, times(0)).findByJobName(eq(jobName));
	}

	@Test
	public void testUpdate_succeedAdmin() throws Exception {

		httpHeaders.add("Authorization", "Bearer " + ADMIN_AUTH_TOKEN);

		JobConfigDto jobConfigDto = jobConfigDtoMockerUpper.create();
		JobConfig jobConfig = jobConfigMockerUpper.create();

		when(jobConfigRepository.existsByJobName(eq(jobConfigDto.getJobName()))).thenReturn(true);
		when(jobConfigRepository.findByJobName(eq(jobConfigDto.getJobName()))).thenReturn(jobConfig);

		mockMvc.perform(put(getContextRoot() + "/jobConfigs/" + jobConfigDto.getJobName()).headers(httpHeaders)
				.contextPath(getContextRoot()).contentType(MediaType.APPLICATION_JSON).content(asJsonString(jobConfigDto)))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.jobName", comparesEqualTo(jobConfigDto.getJobName())));

		verify(jobConfigRepository).save(any(JobConfig.class));
	}

	@Test
	public void testUpdate_failNoAdmin() throws Exception {

		httpHeaders.add("Authorization", "Bearer " + NON_ADMIN_AUTH_TOKEN);

		JobConfigDto jobConfigDto = jobConfigDtoMockerUpper.create();
		JobConfig jobConfig = jobConfigMockerUpper.create();

		when(jobConfigRepository.existsByJobName(eq(jobConfigDto.getJobName()))).thenReturn(true);
		when(jobConfigRepository.findByJobName(eq(jobConfigDto.getJobName()))).thenReturn(jobConfig);

		ResultActions resultActions = mockMvc
				.perform(put(getContextRoot() + "/jobConfigs/" + jobConfigDto.getJobName()).headers(httpHeaders)
						.contextPath(getContextRoot()).contentType(MediaType.APPLICATION_JSON).content(asJsonString(jobConfigDto)))
				.andDo(print()).andExpect(status().isForbidden()).andExpect(jsonPath("$.errors", not(IsEmptyCollection.empty())));

		String s = resultActions.andReturn().getResponse().getContentAsString();
		assertEquals("{\"errors\":{\"accessDenied\":\"Access is denied\"}}", s);

		verify(jobConfigRepository, times(0)).save(any(JobConfig.class));
	}

	@Test
	public void testDelete_succeedAdmin() throws Exception {

		httpHeaders.add("Authorization", "Bearer " + ADMIN_AUTH_TOKEN);

		JobConfigDto jobConfigDto = jobConfigDtoMockerUpper.create();

		when(jobConfigRepository.existsByJobName(eq(jobConfigDto.getJobName()))).thenReturn(true);

		mockMvc.perform(delete(getContextRoot() + "/jobConfigs/" + jobConfigDto.getJobName()).headers(httpHeaders)
				.contextPath(getContextRoot())).andDo(print()).andExpect(status().isOk());

		verify(jobConfigRepository).deleteByJobName(eq(jobConfigDto.getJobName()));
	}

	@Test
	public void testDelete_failNoAdmin() throws Exception {

		httpHeaders.add("Authorization", "Bearer " + NON_ADMIN_AUTH_TOKEN);

		JobConfigDto jobConfigDto = jobConfigDtoMockerUpper.create();

		when(jobConfigRepository.existsByJobName(eq(jobConfigDto.getJobName()))).thenReturn(true);

		ResultActions resultActions = mockMvc
				.perform(delete(getContextRoot() + "/jobConfigs/" + jobConfigDto.getJobName()).headers(httpHeaders)
						.contextPath(getContextRoot()))
				.andDo(print()).andExpect(status().isForbidden()).andExpect(jsonPath("$.errors", not(IsEmptyCollection.empty())));

		String s = resultActions.andReturn().getResponse().getContentAsString();
		assertEquals("{\"errors\":{\"accessDenied\":\"Access is denied\"}}", s);

		verify(jobConfigRepository, times(0)).deleteByJobName(eq(jobConfigDto.getJobName()));
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
