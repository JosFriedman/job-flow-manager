package gov.nyc.doitt.jobstatemanager.domain.jobstate;

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
import gov.nyc.doitt.jobstatemanager.domain.jobstate.JobStateDtoMapper;
import gov.nyc.doitt.jobstatemanager.domain.jobstate.JobStateRepository;
import gov.nyc.doitt.jobstatemanager.domain.jobstate.JobStateService;
import gov.nyc.doitt.jobstatemanager.domain.jobstate.dto.JobStateDto;
import gov.nyc.doitt.jobstatemanager.domain.jobstate.model.JobState;
import gov.nyc.doitt.jobstatemanager.domain.jobstate.model.JobStateMockerUpper;
import gov.nyc.doitt.jobstatemanager.domain.jobstate.model.JobStatus;

@RunWith(SpringRunner.class)
public class JobStateServiceTest extends TestBase {

	@Autowired
	private JobStateMockerUpper JobFlowMockerUpper;

	@Mock
	private JobStateRepository jobStateRepository;

	@Autowired
	private JobStateDtoMapper jobStateDtoMapper;

	@Spy
	@InjectMocks
	private JobStateService jobStateService = new JobStateService();

	@Value("${jobstatemanager.domain.jobflow.JobFlowService.maxBatchSize}")
	private int maxBatchSize;

	@Value("${jobstatemanager.domain.jobflow.JobFlowService.maxRetriesForError}")
	private int maxRetriesForError;

	private Pageable pageable;

	@Before
	public void init() throws Exception {

		pageable = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "jobCreatedTimestamp"));
		FieldUtils.writeField(jobStateService, "pageRequest", pageable, true);
		FieldUtils.writeField(jobStateService, "maxRetriesForError", maxRetriesForError, true);
		FieldUtils.writeField(jobStateService, "jobStateDtoMapper", jobStateDtoMapper, true);
	}

	@Test
	public void testJobFlowServiceNoJobFlows() {

		String appId = "myApp";

		List<JobState> jobStates = Collections.emptyList();
		when(jobStateRepository.findByAppIdAndStatusInAndErrorCountLessThan(eq(appId), ArgumentMatchers.<JobStatus>anyList(),
				eq(maxRetriesForError), eq(pageable))).thenReturn(jobStates);

		List<JobStateDto> batchOfJobFlowDtos = jobStateService.getNextBatch(appId);

		verify(jobStateRepository, times(1)).findByAppIdAndStatusInAndErrorCountLessThan(eq(appId),
				ArgumentMatchers.<JobStatus>anyList(), anyInt(), any(Pageable.class));
		assertTrue(batchOfJobFlowDtos.isEmpty());
	}

	@Test
	public void testJobFlowServiceWithJobFlows() throws Exception {

		String appId = "myApp";

		int listSize = 5;
		List<JobState> jobStates = JobFlowMockerUpper.createList(listSize);
		when(jobStateRepository.findByAppIdAndStatusInAndErrorCountLessThan(eq(appId), ArgumentMatchers.<JobStatus>anyList(),
				anyInt(), any(Pageable.class))).thenReturn(jobStates);

		when(jobStateRepository.existsByAppIdAndJobId(eq(appId), anyString())).thenReturn(true);

		List<JobStateDto> batchOfJobFlowDtos = jobStateService.getNextBatch(appId);

		verify(jobStateRepository, times(1)).findByAppIdAndStatusInAndErrorCountLessThan(eq(appId),
				ArgumentMatchers.<JobStatus>anyList(), anyInt(), any(Pageable.class));
		assertEquals(listSize, batchOfJobFlowDtos.size());

		batchOfJobFlowDtos.forEach(p -> {
			assertEquals(JobStatus.PROCESSING.toString(), p.getStatus());
			assertNotNull(p.getStartTimestamp());
		});
		verify(jobStateRepository, times(listSize)).save(any(JobState.class));
	}

}
