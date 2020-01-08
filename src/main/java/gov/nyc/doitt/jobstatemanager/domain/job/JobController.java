package gov.nyc.doitt.jobstatemanager.domain.job;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
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
import gov.nyc.doitt.jobstatemanager.infrastructure.JobStateManagerException;

@RestController
@RequestMapping("jobStateManager")
public class JobController {

	private Logger logger = LoggerFactory.getLogger(JobController.class);

	@Autowired
	private JobService jobService;
	
	// TODO: validator 

	@PostMapping("/jobs")
	public JobDto createJob(@Valid @RequestBody JobDto jobDto, BindingResult result)
			throws JobStateManagerException {

		logger.debug("createJob: entering: ", jobDto);

		if (result.hasErrors()) {
			throw new JobStateManagerException(result.getFieldErrors());
		}

		return jobService.createJobState(jobDto);
	}

	@GetMapping("/jobs/{appId}")
	public List<JobDto> getJobs(@PathVariable String appId, @RequestParam(defaultValue = "true") boolean nextBatch) {
		return jobService.getJobStates(appId, nextBatch);
	}

	@PatchMapping("/jobs/{appId}")
	public List<JobDto> updateJobs(@PathVariable String appId, @RequestBody List<JobDto> jobDtos) {
		return jobService.patchJobStates(appId, jobDtos);
	}

	@DeleteMapping("/jobs/{appId}/job/{jobId}")
	public String deleteJob(@PathVariable String appId, @PathVariable String jobId) {
		return jobService.deleteJobState(appId, jobId);
	}

	//////////////////////////////////////////////////////
	// other endpoints

	@GetMapping("/jobs")
	public List<JobDto> getJobs() {
		return jobService.getJobStates();
	}

	@GetMapping("/jobs/{appId}/job/{jobId}")
	public JobDto getJob(@PathVariable String appId, @PathVariable String jobId) {
		return jobService.getJobState(appId, jobId);
	}

	@GetMapping("/jobs/batches/{appId}")
	public List<JobDto> getNextBatch(@PathVariable String appId) {
		return jobService.getNextBatch(appId);
	}

	@GetMapping("/jobIds/{appId}")
	public List<String> getJobIds(@PathVariable String appId, @RequestParam(defaultValue = "true") boolean nextBatch) {
		return jobService.getJobIds(appId, nextBatch);
	}

	@PutMapping("/jobs/{appId}/job/{jobId}")
	public JobDto updateJob(@PathVariable String appId, @PathVariable String jobId,
			@Valid @RequestBody JobDto jobDto, BindingResult result) throws JobStateManagerException {

		logger.debug("updateJob: entering: ", jobDto);

		if (result.hasErrors()) {
			throw new JobStateManagerException(result.getFieldErrors());
		}

		return jobService.updateJobState(appId, jobId, jobDto);
	}

}