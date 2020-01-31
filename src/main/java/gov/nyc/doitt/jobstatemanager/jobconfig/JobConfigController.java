package gov.nyc.doitt.jobstatemanager.jobconfig;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nyc.doitt.jobstatemanager.common.JobStateManagerException;
import gov.nyc.doitt.jobstatemanager.common.ValidationException;

@RestController
@RequestMapping("jobConfigs")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class JobConfigController {

	private Logger logger = LoggerFactory.getLogger(JobConfigController.class);

	@Autowired
	private JobConfigService jobConfigService;

	@Autowired
	private JobConfigDtoValidator jobConfigDtoValidator;

	@InitBinder("jobConfigDto")
	private void initBinder_jobConfigDto(WebDataBinder binder) {
		binder.addValidators(jobConfigDtoValidator);
	}

	@PostMapping("")
	public JobConfigDto createJobConfig(@Valid @RequestBody JobConfigDto jobConfigDto, BindingResult result)
			throws JobStateManagerException {

		logger.debug("createJobConfig: entering: {}", jobConfigDto);

		if (result.hasErrors()) {
			throw new ValidationException(result.getFieldErrors());
		}
		return jobConfigService.createJobConfig(jobConfigDto);
	}

	@GetMapping("/{jobName}")
	public JobConfigDto getJobConfig(@PathVariable String jobName) {

		logger.debug("getJobConfig: entering: jobName={}", jobName);

		return jobConfigService.getJobConfig(jobName);
	}

	@GetMapping("")
	public List<JobConfigDto> getJobConfigs() {

		logger.debug("getJobConfigs: entering");

		return jobConfigService.getJobConfigs();
	}

	@PutMapping("/{jobName}")
	public JobConfigDto updateJobConfig(@PathVariable String jobName, @Valid @RequestBody JobConfigDto jobConfigDto) {

		logger.debug("updateJob: updateJobConfig: jobName={}, jobConfigDto={}", jobName, jobConfigDto);

		return jobConfigService.updateJobConfig(jobName, jobConfigDto);
	}

	@DeleteMapping("/{jobName}")
	public String deleteJobConfig(@PathVariable String jobName) {

		logger.debug("deleteJobConfig: entering: jobName={}, jobId={}", jobName);

		return jobConfigService.deleteJobConfig(jobName);
	}
}