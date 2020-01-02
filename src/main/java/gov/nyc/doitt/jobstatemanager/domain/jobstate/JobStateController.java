package gov.nyc.doitt.jobstatemanager.domain.jobstate;

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

import gov.nyc.doitt.jobstatemanager.domain.jobstate.dto.JobStateDto;
import gov.nyc.doitt.jobstatemanager.infrastructure.JobStateManagerException;

@RestController
@RequestMapping("jobStateManager")
public class JobStateController {

	private Logger logger = LoggerFactory.getLogger(JobStateController.class);

	@Autowired
	private JobStateService jobStateService;
	
	// TODO: validator 

	@PostMapping("/jobStates")
	public JobStateDto createJobFlow(@Valid @RequestBody JobStateDto jobStateDto, BindingResult result)
			throws JobStateManagerException {

		logger.debug("createJobFlow: entering: ", jobStateDto);

		if (result.hasErrors()) {
			throw new JobStateManagerException(result.getFieldErrors());
		}

		return jobStateService.createJobState(jobStateDto);
	}

	@GetMapping("/jobStates/{appId}")
	public List<JobStateDto> getJobFlows(@PathVariable String appId, @RequestParam(defaultValue = "true") boolean nextBatch) {
		return jobStateService.getJobStates(appId, nextBatch);
	}

	@PatchMapping("/jobStates/{appId}")
	public List<JobStateDto> updateJobFlows(@PathVariable String appId, @RequestBody List<JobStateDto> jobStateDtos) {
		return jobStateService.patchJobStates(appId, jobStateDtos);
	}

	@DeleteMapping("/jobStates/{appId}/job/{jobId}")
	public String deleteJobFlow(@PathVariable String appId, @PathVariable String jobId) {
		return jobStateService.deleteJobState(appId, jobId);
	}

	//////////////////////////////////////////////////////
	// other endpoints

	@GetMapping("/jobStates")
	public List<JobStateDto> getJobFlows() {
		return jobStateService.getJobStates();
	}

	@GetMapping("/jobStates/{appId}/job/{jobId}")
	public JobStateDto getJobFlow(@PathVariable String appId, @PathVariable String jobId) {
		return jobStateService.getJobState(appId, jobId);
	}

	@GetMapping("/jobStates/batches/{appId}")
	public List<JobStateDto> getNextBatch(@PathVariable String appId) {
		return jobStateService.getNextBatch(appId);
	}

	@GetMapping("/jobStateIds/{appId}")
	public List<String> getJobFlowIds(@PathVariable String appId, @RequestParam(defaultValue = "true") boolean nextBatch) {
		return jobStateService.getJobIds(appId, nextBatch);
	}

	@PutMapping("/jobStates/{appId}/job/{jobId}")
	public JobStateDto updateJobFlow(@PathVariable String appId, @PathVariable String jobId,
			@Valid @RequestBody JobStateDto jobStateDto, BindingResult result) throws JobStateManagerException {

		logger.debug("updateJobFlow: entering: ", jobStateDto);

		if (result.hasErrors()) {
			throw new JobStateManagerException(result.getFieldErrors());
		}

		return jobStateService.updateJobState(appId, jobId, jobStateDto);
	}

}
