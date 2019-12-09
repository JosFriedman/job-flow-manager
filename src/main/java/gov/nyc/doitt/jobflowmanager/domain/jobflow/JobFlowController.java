package gov.nyc.doitt.jobflowmanager.domain.jobflow;

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

import gov.nyc.doitt.jobflowmanager.domain.jobflow.dto.JobFlowDto;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobFlow;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobStatus;
import gov.nyc.doitt.jobflowmanager.infrastructure.JobFlowManagerException;
import javafx.util.Pair;

@RestController
@RequestMapping("jobFlowManager")
public class JobFlowController {

	private Logger logger = LoggerFactory.getLogger(JobFlowController.class);

	@Autowired
	private JobFlowService jobFlowService;

	@GetMapping("/jobFlows")
	public List<JobFlowDto> getJobFlows() {
		return jobFlowService.getJobFlows();
	}

	@GetMapping("/jobFlows/{appId}/job/{jobId}")
	public JobFlowDto getJobFlow(@PathVariable String appId, @PathVariable String jobId) {
		return jobFlowService.getJobFlow(appId, jobId);
	}

	@GetMapping("/jobFlows/batches/{appId}")
	public List<JobFlowDto> getNextBatch(@PathVariable String appId) {
		return jobFlowService.getNextBatch(appId);
	}

	@PostMapping("/jobFlows")
	public JobFlowDto createJobFlow(@Valid @RequestBody JobFlowDto jobFlowDto, BindingResult result)
			throws JobFlowManagerException {

		logger.debug("createJobFlow: entering: ", jobFlowDto);

		if (result.hasErrors()) {
			throw new JobFlowManagerException(result.getFieldErrors());
		}

		return jobFlowService.createJobFlow(jobFlowDto);
	}

	@PutMapping("/jobFlows/{appId}/job/{jobId}")
	public JobFlowDto updateJobFlow(@PathVariable String appId, @PathVariable String jobId, @Valid @RequestBody JobFlowDto jobFlowDto, BindingResult result)
			throws JobFlowManagerException {

		logger.debug("updateJobFlow: entering: ", jobFlowDto);

		if (result.hasErrors()) {
			throw new JobFlowManagerException(result.getFieldErrors());
		}

		return jobFlowService.updateJobFlow(appId, jobId, jobFlowDto);
	}

	@DeleteMapping("/jobFlows/{appId}/job/{jobId}")
	public String deleteJobFlow(@PathVariable String appId, @PathVariable String jobId) {
		return jobFlowService.deleteJobFlow(appId, jobId);
	}


	@GetMapping("/jobFlowIds/{appId}")
	public List<String> getJobFlowIds(@PathVariable String appId, @RequestParam(defaultValue = "true") boolean nextBatch) {
		return jobFlowService.getJobIds(appId, nextBatch);
	}

	@PatchMapping("/jobFlows/{appId}")
	public List<JobFlowDto> updateJobFlows(@PathVariable String appId,  @RequestBody List<JobFlowDto> jobFlowDtos) {
		return jobFlowService.patchJobFlows(appId, jobFlowDtos);
	}


}
