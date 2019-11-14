package gov.nyc.doitt.jobflowmanager.domain.jobflow;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nyc.doitt.jobflowmanager.domain.jobflow.dto.JobFlowDto;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobFlow;
import gov.nyc.doitt.jobstatusmanager.infrastructure.JobFlowManagerException;

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
		return jobFlowDtoMapper.toDto(jobFlowService.getAll());
	}

	@GetMapping("/jobFlows/batches")
	public List<JobFlowDto> getNextBatch(String appId) {
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

	@PutMapping("/jobFlows")
	public JobFlowDto updateJobFlow(@Valid @RequestBody JobFlowDto jobFlowDto, BindingResult result)
			throws JobFlowManagerException {

		logger.debug("updateJobFlow: entering: ", jobFlowDto);

		if (result.hasErrors()) {
			throw new JobFlowManagerException(result.getFieldErrors());
		}

		JobFlow jobFlow = jobFlowService.updateJobFlow(jobFlowDtoMapper.fromDto(jobFlowDto));
		JobFlowDto responseJobFlowDto = jobFlowDtoMapper.toDto(jobFlow);
		return responseJobFlowDto;
	}

}
