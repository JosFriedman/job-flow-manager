package gov.nyc.doitt.jobstatemanager.domain.job;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.nyc.doitt.jobstatemanager.domain.job.dto.JobDto;
import gov.nyc.doitt.jobstatemanager.domain.job.model.JobState;
import gov.nyc.doitt.jobstatemanager.infrastructure.JobStateManagerException;
import gov.nyc.doitt.jobstatemanager.infrastructure.ValidationException;

@RestController
@RequestMapping("jobStateManager")
public class JobController {

	private Logger logger = LoggerFactory.getLogger(JobController.class);

	@Autowired
	private JobService jobService;

	@PostMapping("/jobs")
	public JobDto createJob(@Valid @RequestBody JobDto jobDto) throws JobStateManagerException {

		logger.debug("createJob: entering: ", jobDto);

		return jobService.createJob(jobDto);
	}

	@GetMapping("/jobs/{appId}")
	public List<JobDto> getJobs(@PathVariable String appId, @RequestParam(defaultValue = "false") boolean nextBatch, @RequestParam(required = false)  String state) {
		
		if (nextBatch && !StringUtils.isBlank(state)) {
			throw new ValidationException("nextBatch and state cannot be both specified");
		}
		if (nextBatch) {
			return jobService.getNextBatch(appId);
		}
		if (!StringUtils.isBlank(state)) {
			return jobService.getJobs(appId, JobState.valueOf(state));			
		}
		return jobService.getJobs(appId);
	}

	@PatchMapping("/jobs/{appId}")
	public List<JobDto> patchJobs(@PathVariable String appId, @RequestBody List<JobDto> jobDtos) {
		return jobService.updateJobsWithResults(appId, jobDtos);
	}

	@DeleteMapping("/jobs/{appId}/job/{jobId}")
	public String deleteJob(@PathVariable String appId, @PathVariable String jobId) {
		return jobService.deleteJob(appId, jobId);
	}

	//////////////////////////////////////////////////////
	// other endpoints

	@GetMapping("/jobs")
	public List<JobDto> getJobs() {
		return jobService.getJobs();
	}

	@GetMapping("/jobs/{appId}/job/{jobId}")
	public JobDto getJob(@PathVariable String appId, @PathVariable String jobId) {
		return jobService.getJob(appId, jobId);
	}

	@GetMapping("/jobIds/{appId}")
	public List<String> getJobIds(@PathVariable String appId, @RequestParam(defaultValue = "true") boolean nextBatch) {
		return jobService.getJobIds(appId, nextBatch);
	}

	@PutMapping("/jobs/{appId}/job/{jobId}")
	public JobDto updateJob(@PathVariable String appId, @PathVariable String jobId, @Valid @RequestBody JobDto jobDto)
			throws JobStateManagerException {

		logger.debug("updateJob: entering: ", jobDto);

		return jobService.updateJob(appId, jobId, jobDto);
	}

}
