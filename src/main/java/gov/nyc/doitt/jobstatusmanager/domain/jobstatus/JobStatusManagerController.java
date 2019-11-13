package gov.nyc.doitt.jobstatusmanager.domain.jobstatus;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nyc.doitt.jobstatusmanager.domain.jobstatus.model.JobStatus;

@RestController
@RequestMapping("jobStatusManager")
public class JobStatusManagerController {

	@Autowired
	private JobStatusService jobStatusService;

	@GetMapping("/jobStatuses")
	public List<JobStatus> getJobStatuses() {
		return jobStatusService.getAll();
	}

	@GetMapping("/jobStatuses/batches")
	public List<JobStatus> getNextBatch() {
		return jobStatusService.getNextBatch();
	}

}
