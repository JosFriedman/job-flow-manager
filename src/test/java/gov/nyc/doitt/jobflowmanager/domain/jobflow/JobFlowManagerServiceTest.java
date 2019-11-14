package gov.nyc.doitt.jobflowmanager.domain.jobflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import gov.nyc.doitt.jobflowmanager.TestBase;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobFlow;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobFlowMockerUpper;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobStatus;

@RunWith(SpringRunner.class)
public class JobFlowManagerServiceTest extends TestBase {

	@Autowired
	private JobFlowMockerUpper cmiiSubmissionMockerUpper;

	@Mock
	private JobFlowRepository cmiiSubmissionRepository;

	@Spy
	@InjectMocks
	private JobFlowService cmiiSubmissionService = new JobFlowService();

	@Value("${jobflowmanager.domain.jobflow.JobFlowService.maxBatchSize}")
	private int maxBatchSize;

	@Value("${jobflowmanager.domain.jobflow.JobFlowService.maxRetriesForError}")
	private int maxRetriesForError;

	private Pageable pageable;

	@Before
	public void init() throws Exception {

		pageable = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "submitted"));
		FieldUtils.writeField(cmiiSubmissionService, "pageRequest", pageable, true);
		FieldUtils.writeField(cmiiSubmissionService, "maxRetriesForError", maxRetriesForError, true);
	}

	@Test
	public void testSubmitterServiceNoSubmissions() {

		List<JobFlow> cmiiSubmissions = Collections.emptyList();
		when(cmiiSubmissionRepository.findByStatusInAndErrorCountLessThan(ArgumentMatchers.<JobStatus>anyList(),
				eq(maxRetriesForError), eq(pageable))).thenReturn(cmiiSubmissions);

		List<JobFlow> batchOfJobFlows = cmiiSubmissionService.getNextBatch();

		verify(cmiiSubmissionRepository, times(1)).findByStatusInAndErrorCountLessThan(
				ArgumentMatchers.<JobStatus>anyList(), anyInt(), any(Pageable.class));
		assertTrue(batchOfJobFlows.isEmpty());
	}

	@Test
	public void testSubmitterServiceWithSubmissions() throws Exception {

		int listSize = 5;
		List<JobFlow> cmiiSubmissions = cmiiSubmissionMockerUpper.createList(listSize);
		when(cmiiSubmissionRepository.findByStatusInAndErrorCountLessThan(ArgumentMatchers.<JobStatus>anyList(),
				eq(maxRetriesForError), eq(pageable))).thenReturn(cmiiSubmissions);

		List<JobFlow> batchOfJobFlows = cmiiSubmissionService.getNextBatch();

		verify(cmiiSubmissionRepository, times(1)).findByStatusInAndErrorCountLessThan(
				ArgumentMatchers.<JobStatus>anyList(), anyInt(), any(Pageable.class));
		assertTrue(batchOfJobFlows.isEmpty());

		batchOfJobFlows.forEach(p -> {
			assertEquals(JobStatus.PROCESSING, p.getStatus());
			assertNotNull(p.getStartTimestamp());
			verify(cmiiSubmissionService).updateJobFlow(p);
		});
	}

}
