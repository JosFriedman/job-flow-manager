package gov.nyc.doitt.jobstatemanager.task;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nyc.doitt.jobstatemanager.job.Job;
import gov.nyc.doitt.jobstatemanager.job.JobDtoMockerUpper;
import gov.nyc.doitt.jobstatemanager.job.JobMockerUpper;
import gov.nyc.doitt.jobstatemanager.job.JobRepository;
import gov.nyc.doitt.jobstatemanager.job.JobState;
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfig;
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfigDtoMockerUpper;
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfigMockerUpper;
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfigService;
import gov.nyc.doitt.jobstatemanager.jobconfig.TaskConfig;
import gov.nyc.doitt.jobstatemanager.security.JobAuthenticationManager;
import gov.nyc.doitt.jobstatemanager.security.JobAuthorizer;
import gov.nyc.doitt.jobstatemanager.test.BaseTest;

@RunWith(SpringJUnit4ClassRunner.class)
public class TaskControllerTest extends BaseTest {

	// mocking/unmocking of this bean is done explicitly below
	private JobConfigService jobConfigService;

	@Autowired
	private JobDtoMockerUpper jobDtoMockerUpper;

	@Autowired
	private JobMockerUpper jobMockerUpper;

	@Autowired
	private JobConfigMockerUpper jobConfigMockerUpper;

	@Autowired
	private JobConfigDtoMockerUpper jobConfigDtoMockerUpper;

	@Autowired
	@InjectMocks
	private TaskService taskService;

	@Autowired
	@InjectMocks
	private JobAuthorizer jobAuthorizer;

	@Autowired
	@InjectMocks
	private JobAuthenticationManager jobAuthenticationManager;

	// mocking/unmocking of this bean is done explicitly below
	private JobRepository jobRepository;

	private MockMvc mockMvc;

	private HttpHeaders httpHeaders;

	private static final String NON_ADMIN_AUTH_TOKEN = "RV9do3MRUY3gw1aclo-J#cAi6xQCJzqE-B9#LhCL)U+)jE%`eMek)4m9FSuG~y+w";

	private static final String ADMIN_AUTH_TOKEN = "G0Ts6!yeH^uJuLIaa`J2=W#+t~p-faEgw=~Fyp0qXY778IyJAUs+^PU)=OZBiayn";

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).apply(springSecurity()).build();

		jobRepository = mock(JobRepository.class);
		ReflectionTestUtils.setField(taskService, "jobRepository", jobRepository);
		jobConfigService = mock(JobConfigService.class);
		ReflectionTestUtils.setField(taskService, "jobConfigService", jobConfigService);

		MockitoAnnotations.initMocks(this);

		httpHeaders = new HttpHeaders();
	}

	@After
	public void tearDown() throws Exception {

		// put back real JobRepository bean into TaskService
		jobRepository = getApplicationContext().getBean(JobRepository.class);
		ReflectionTestUtils.setField(taskService, "jobRepository", jobRepository);

		// put back real jobConfigService bean into JobAuthorizer
		jobConfigService = getApplicationContext().getBean(JobConfigService.class);
		ReflectionTestUtils.setField(jobAuthorizer, "jobConfigService", jobConfigService);
	}

	@Test
	public void testStartTasks_succeed() throws Exception {

		httpHeaders.add("Authorization", "Bearer " + NON_ADMIN_AUTH_TOKEN);

		List<Job> jobs = jobMockerUpper.createList(10);
		String jobName = jobs.get(0).getJobName();

		JobConfig jobConfig = jobConfigMockerUpper.create(jobName);
		TaskConfig taskConfig = jobConfig.getTaskConfigs().get(0);
		String taskName = taskConfig.getName();

		when(jobRepository.findByJobNameAndStateInAndNextTaskName(eq(jobName), ArgumentMatchers.anyList(), eq(taskName),
				any(PageRequest.class))).thenReturn(jobs);

		when(jobConfigService.existsJobConfig(jobName)).thenReturn(true);
		when(jobConfigService.getJobConfigDomain(jobName)).thenReturn(jobConfig);

		ResultActions resultActions = mockMvc
				.perform(post(getContextRoot() + "/tasks" + "?jobName=" + jobName + "&taskName=" + taskName).headers(httpHeaders)
						.contentType(MediaType.APPLICATION_JSON).headers(httpHeaders).contextPath(getContextRoot()))
				.andDo(print()).andExpect(status().isOk());

		String content = resultActions.andReturn().getResponse().getContentAsString();
		List<TaskDto> taskDtos = taskDtosJsonAsObject(content);

		jobs.forEach(p -> {
			assertEquals(JobState.PROCESSING, p.getState());
			assertEquals(1, p.getTasks().size());
			assertEquals(TaskState.PROCESSING, p.getTasks().get(0).getState());
		});

		taskDtos.forEach(p -> {
			assertEquals(TaskState.PROCESSING.toString(), p.getState());
		});

		verify(jobRepository, times(jobs.size())).save(any(Job.class));
	}
	@Test
	public void testStartTasks_failNoMatchAuthToken() throws Exception {

		httpHeaders.add("Authorization", "Bearer " + "wrong_token");

		List<Job> jobs = jobMockerUpper.createList(10);
		String jobName = jobs.get(0).getJobName();

		JobConfig jobConfig = jobConfigMockerUpper.create(jobName);
		TaskConfig taskConfig = jobConfig.getTaskConfigs().get(0);
		String taskName = taskConfig.getName();

		when(jobRepository.findByJobNameAndStateInAndNextTaskName(eq(jobName), ArgumentMatchers.anyList(), eq(taskName),
				any(PageRequest.class))).thenReturn(jobs);

		when(jobConfigService.existsJobConfig(jobName)).thenReturn(true);
		when(jobConfigService.getJobConfigDomain(jobName)).thenReturn(jobConfig);

		ResultActions resultActions = mockMvc
				.perform(post(getContextRoot() + "/tasks" + "?jobName=" + jobName + "&taskName=" + taskName).headers(httpHeaders)
						.contentType(MediaType.APPLICATION_JSON).headers(httpHeaders).contextPath(getContextRoot()))
				.andDo(print()).andExpect(status().isForbidden()).andExpect(jsonPath("$.errors").isNotEmpty());

		String s = resultActions.andReturn().getResponse().getContentAsString();
		assertEquals("{\"errors\":{\"accessDenied\":\"Access is denied\"}}", s);

		verify(jobRepository, times(0)).save(any(Job.class));
	}

	private List<TaskDto> taskDtosJsonAsObject(String json) {
		try {
			return new ObjectMapper().readValue(json, new TypeReference<List<TaskDto>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
