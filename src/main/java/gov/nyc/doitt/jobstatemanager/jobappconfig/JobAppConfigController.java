package gov.nyc.doitt.jobstatemanager.jobappconfig;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nyc.doitt.jobstatemanager.common.JobStateManagerException;

@RestController
@RequestMapping("jobAppConfigs")
public class JobAppConfigController {

	private Logger logger = LoggerFactory.getLogger(JobAppConfigController.class);

	@Autowired
	private JobAppConfigService jobAppConfigService;

	@PostMapping("")
	public JobAppConfigDto createJobAppConfig(@Valid @RequestBody JobAppConfigDto jobAppConfigDto) throws JobStateManagerException {

		logger.debug("createJobAppConfig: entering: {}", jobAppConfigDto);

		return jobAppConfigService.createJobAppConfig(jobAppConfigDto);
	}

	@GetMapping("/{appName}")
	public JobAppConfigDto getJobAppConfig(@PathVariable String appName) {

		logger.debug("getJobAppConfig: entering: appName={}", appName);

		return jobAppConfigService.getJobAppConfig(appName);
	}

	@GetMapping("")
	public List<JobAppConfigDto> getJobAppConfigs() {
		return jobAppConfigService.getJobAppConfigs();
	}

	@PutMapping("/{appName}")
	public JobAppConfigDto updateJobAppConfig(@PathVariable String appName, @Valid @RequestBody JobAppConfigDto jobAppConfigDto) {

		logger.debug("updateJob: updateJobAppConfig: appName={}, jobAppConfigDto={}", appName, jobAppConfigDto);

		return jobAppConfigService.updateJobAppConfig(appName, jobAppConfigDto);
	}

	@DeleteMapping("/{appName}")
	public String deleteJobAppConfig(@PathVariable String appName) {

		logger.debug("deleteJobAppConfig: entering: appName={}, jobId={}", appName);

		return jobAppConfigService.deleteJobAppConfig(appName);
	}
}