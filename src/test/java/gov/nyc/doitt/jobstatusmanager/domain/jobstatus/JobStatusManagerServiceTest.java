package gov.nyc.doitt.jobstatusmanager.domain.jobstatus;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import gov.nyc.doitt.jobstatusmanager.TestBase;
import gov.nyc.doitt.jobstatusmanager.domain.jobstatus.JobStatusService;
import gov.nyc.doitt.jobstatusmanager.domain.jobstatus.JobStatusRepository;
import gov.nyc.doitt.jobstatusmanager.domain.jobstatus.model.JobStatus;
import gov.nyc.doitt.jobstatusmanager.domain.jobstatus.model.JobStatusMockerUpper;
import gov.nyc.doitt.jobstatusmanager.domain.jobstatus.model.JobStatusType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JobStatusManagerServiceTest extends TestBase {

	@Autowired
	private JobStatusMockerUpper cmiiSubmissionMockerUpper;

	@Mock
	private JobStatusRepository cmiiSubmissionRepository;

	@Spy
	@InjectMocks
	private JobStatusService cmiiSubmissionService = new JobStatusService();

	@Value("${jobstatusmanager.domain.JobStatusManagerService.maxBatchSize}")
	private int maxBatchSize;

	@Value("${jobstatusmanager.domain.JobStatusManagerService.maxRetriesForError}")
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

		List<JobStatus> cmiiSubmissions = Collections.emptyList();
		when(cmiiSubmissionRepository.findByStatusInAndErrorCountLessThan(ArgumentMatchers.<JobStatusType>anyList(),
				eq(maxRetriesForError), eq(pageable))).thenReturn(cmiiSubmissions);

		List<JobStatus> batchOfJobStatuss = cmiiSubmissionService.getNextBatch();

		verify(cmiiSubmissionRepository, times(1)).findByStatusInAndErrorCountLessThan(
				ArgumentMatchers.<JobStatusType>anyList(), anyInt(), any(Pageable.class));
		assertTrue(batchOfJobStatuss.isEmpty());
	}

	@Test
	public void testSubmitterServiceWithSubmissions() throws Exception {

		int listSize = 5;
		List<JobStatus> cmiiSubmissions = cmiiSubmissionMockerUpper.createList(listSize);
		when(cmiiSubmissionRepository.findByStatusInAndErrorCountLessThan(ArgumentMatchers.<JobStatusType>anyList(),
				eq(maxRetriesForError), eq(pageable))).thenReturn(cmiiSubmissions);

		List<JobStatus> batchOfJobStatuss = cmiiSubmissionService.getNextBatch();

		verify(cmiiSubmissionRepository, times(1)).findByStatusInAndErrorCountLessThan(
				ArgumentMatchers.<JobStatusType>anyList(), anyInt(), any(Pageable.class));
		assertTrue(batchOfJobStatuss.isEmpty());

		batchOfJobStatuss.forEach(p -> {
			assertEquals(JobStatusType.PROCESSING, p.getStatus());
			assertNotNull(p.getStartTimestamp());
			verify(cmiiSubmissionService).updateJobStatus(p);
		});
	}

}
