package gov.nyc.doitt.jobstatusmanager.domain.jobstatus;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nyc.doitt.jobstatusmanager.domain.jobstatus.dto.JobStatusDto;
import gov.nyc.doitt.jobstatusmanager.domain.jobstatus.model.JobStatus;

@RestController
@RequestMapping("jobStatusManager")
public class JobStatusController {

	private Logger logger = LoggerFactory.getLogger(JobStatusController.class);

	@Autowired
	private JobStatusService jobStatusService;

	@Autowired
	private JobStatusDtoMapper jobStatusDtoMapper;

	@GetMapping("/jobStatuses")
	public List<JobStatus> getJobStatuses() {
		return jobStatusService.getAll();
	}

	@GetMapping("/jobStatuses/batches")
	public List<JobStatusDto> getNextBatch() {
		return jobStatusDtoMapper.toDto(jobStatusService.getNextBatch());
	}

	@PostMapping("/jobStatuses")
	public JobStatusDto createJobStatus(@Valid @RequestBody JobStatusDto jobStatusDto, BindingResult result)
			throws JobStatusManagerException {

		logger.debug("createJobStatus: entering: ", jobStatusDto);

		if (result.hasErrors()) {
			throw new JobStatusManagerException(result.getFieldErrors());
		}

		JobStatus jobStatus = jobStatusService.createJobStatus(jobStatusDtoMapper.fromDto(jobStatusDto));
		JobStatusDto responseJobStatusDto = jobStatusDtoMapper.toDto(jobStatus);
		return responseJobStatusDto;
	}

}
