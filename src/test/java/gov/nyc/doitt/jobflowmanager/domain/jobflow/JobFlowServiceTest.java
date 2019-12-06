package gov.nyc.doitt.jobflowmanager.domain.jobflow;

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

import gov.nyc.doitt.jobflowmanager.TestBase;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.dto.JobFlowDto;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobFlow;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobFlowMockerUpper;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobStatus;

@RunWith(SpringRunner.class)
public class JobFlowServiceTest extends TestBase {

	@Autowired
	private JobFlowMockerUpper JobFlowMockerUpper;

	@Mock
	private JobFlowRepository jobFlowRepository;

	@Autowired
	private JobFlowDtoMapper jobFlowDtoMapper;

	@Spy
	@InjectMocks
	private JobFlowService jobFlowService = new JobFlowService();

	@Value("${jobflowmanager.domain.jobflow.JobFlowService.maxBatchSize}")
	private int maxBatchSize;

	@Value("${jobflowmanager.domain.jobflow.JobFlowService.maxRetriesForError}")
	private int maxRetriesForError;

	private Pageable pageable;

	@Before
	public void init() throws Exception {

		pageable = PageRequest.of(0, maxBatchSize, Sort.by(Sort.Direction.ASC, "jobCreatedTimestamp"));
		FieldUtils.writeField(jobFlowService, "pageRequest", pageable, true);
		FieldUtils.writeField(jobFlowService, "maxRetriesForError", maxRetriesForError, true);
		FieldUtils.writeField(jobFlowService, "jobFlowDtoMapper", jobFlowDtoMapper, true);
	}

	@Test
	public void testJobFlowServiceNoJobFlows() {

		String appId = "myApp";

		List<JobFlow> jobFlows = Collections.emptyList();
		when(jobFlowRepository.findByAppIdAndStatusInAndErrorCountLessThan(eq(appId), ArgumentMatchers.<JobStatus>anyList(),
				eq(maxRetriesForError), eq(pageable))).thenReturn(jobFlows);

		List<JobFlowDto> batchOfJobFlowDtos = jobFlowService.getNextBatch(appId);

		verify(jobFlowRepository, times(1)).findByAppIdAndStatusInAndErrorCountLessThan(eq(appId),
				ArgumentMatchers.<JobStatus>anyList(), anyInt(), any(Pageable.class));
		assertTrue(batchOfJobFlowDtos.isEmpty());
	}

	@Test
	public void testJobFlowServiceWithJobFlows() throws Exception {

		String appId = "myApp";

		int listSize = 5;
		List<JobFlow> jobFlows = JobFlowMockerUpper.createList(listSize);
		when(jobFlowRepository.findByAppIdAndStatusInAndErrorCountLessThan(eq(appId), ArgumentMatchers.<JobStatus>anyList(),
				anyInt(), any(Pageable.class))).thenReturn(jobFlows);

		when(jobFlowRepository.existsByAppIdAndJobId(eq(appId), anyString())).thenReturn(true);

		List<JobFlowDto> batchOfJobFlowDtos = jobFlowService.getNextBatch(appId);

		verify(jobFlowRepository, times(1)).findByAppIdAndStatusInAndErrorCountLessThan(eq(appId),
				ArgumentMatchers.<JobStatus>anyList(), anyInt(), any(Pageable.class));
		assertEquals(listSize, batchOfJobFlowDtos.size());

		batchOfJobFlowDtos.forEach(p -> {
			assertEquals(JobStatus.PROCESSING.toString(), p.getStatus());
			assertNotNull(p.getStartTimestamp());
		});
		verify(jobFlowRepository, times(listSize)).save(any(JobFlow.class));
	}

}
