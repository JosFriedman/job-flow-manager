package gov.nyc.doitt.jobflowmanager.domain.jobflow;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nyc.doitt.jobflowmanager.domain.jobflow.dto.JobFlowDto;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobFlow;
import gov.nyc.doitt.jobflowmanager.infrastructure.JobFlowManagerException;

@RestController
@RequestMapping("jobFlowManager")
public class JobFlowController {

	private Logger logger = LoggerFactory.getLogger(JobFlowController.class);

	@Autowired
	private JobFlowService jobFlowService;

	@Autowired
	private JobFlowDtoMapper jobFlowDtoMapper;

	@GetMapping("/jobFlows")
	public List<JobFlowDto> getJobFlows() {
		return jobFlowDtoMapper.toDto(jobFlowService.getJobFlows());
	}

	@GetMapping("/jobFlows/{appId}/job/{jobId}")
	public JobFlowDto getJobFlow(@PathVariable String appId, @PathVariable String jobId) {
		return jobFlowDtoMapper.toDto(jobFlowService.getJobFlow(appId, jobId));
	}

	@GetMapping("/jobFlows/batches/{appId}")
	public List<JobFlowDto> getNextBatch(@PathVariable String appId) {
		return jobFlowDtoMapper.toDto(jobFlowService.getNextBatch(appId));
	}

	@PostMapping("/jobFlows")
	public JobFlowDto createJobFlow(@Valid @RequestBody JobFlowDto jobFlowDto, BindingResult result)
			throws JobFlowManagerException {

		logger.debug("createJobFlow: entering: ", jobFlowDto);

		if (result.hasErrors()) {
			throw new JobFlowManagerException(result.getFieldErrors());
		}

		JobFlow jobFlow = jobFlowService.createJobFlow(jobFlowDtoMapper.fromDto(jobFlowDto));
		JobFlowDto responseJobFlowDto = jobFlowDtoMapper.toDto(jobFlow);
		return responseJobFlowDto;
	}

	@PutMapping("/jobFlows/{appId}/job/{jobId}")
	public JobFlowDto updateJobFlow(@PathVariable String appId, @PathVariable String jobId, @Valid @RequestBody JobFlowDto jobFlowDto, BindingResult result)
			throws JobFlowManagerException {

		logger.debug("updateJobFlow: entering: ", jobFlowDto);

		if (result.hasErrors()) {
			throw new JobFlowManagerException(result.getFieldErrors());
		}

		JobFlow jobFlow = jobFlowService.updateJobFlow(appId, jobId, jobFlowDto);
		JobFlowDto responseJobFlowDto = jobFlowDtoMapper.toDto(jobFlow);
		return responseJobFlowDto;
	}

	@DeleteMapping("/jobFlows/{appId}/job/{jobId}")
	public String deleteJobFlow(@PathVariable String appId, @PathVariable String jobId) {
		return jobFlowService.deleteJobFlow(appId, jobId);
	}


}
