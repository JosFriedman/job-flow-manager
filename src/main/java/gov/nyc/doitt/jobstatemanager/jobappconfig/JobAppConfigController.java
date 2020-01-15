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
@RequestMapping("jobStateManager")
public class JobAppConfigController {

	private Logger logger = LoggerFactory.getLogger(JobAppConfigController.class);

	@Autowired
	private JobAppConfigService jobAppConfigService;

	@PostMapping("/jobAppConfigs")
	public JobAppConfigDto createJobAppConfig(@Valid @RequestBody JobAppConfigDto jobAppConfigDto) throws JobStateManagerException {

		logger.debug("createJobAppConfig: entering: {}", jobAppConfigDto);

		return jobAppConfigService.createJobAppConfig(jobAppConfigDto);
	}

	@GetMapping("/jobAppConfigs/{appId}")
	public JobAppConfigDto getJobAppConfig(@PathVariable String appId) {

		logger.debug("getJobAppConfig: entering: appId={}", appId);

		return jobAppConfigService.getJobAppConfig(appId);
	}

	@GetMapping("/jobAppConfigs")
	public List<JobAppConfigDto> getJobAppConfigs() {
		return jobAppConfigService.getJobAppConfigs();
	}

	@PutMapping("/jobAppConfigs/{appId}")
	public JobAppConfigDto updateJobAppConfig(@PathVariable String appId, @Valid @RequestBody JobAppConfigDto jobAppConfigDto) {

		logger.debug("updateJob: updateJobAppConfig: appId={}, jobAppConfigDto={}", appId, jobAppConfigDto);

		return jobAppConfigService.updateJobAppConfig(appId, jobAppConfigDto);
	}

	@DeleteMapping("/jobAppConfigs/{appId}")
	public String deleteJobAppConfig(@PathVariable String appId) {

		logger.debug("deleteJobAppConfig: entering: appId={}, jobId={}", appId);

		return jobAppConfigService.deleteJobAppConfig(appId);
	}
}