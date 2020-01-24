package gov.nyc.doitt.jobstatemanager.job;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nyc.doitt.jobstatemanager.common.JobStateManagerException;
import gov.nyc.doitt.jobstatemanager.common.ValidationException;

@RestController
@RequestMapping("jobs")
public class JobController {

	private Logger logger = LoggerFactory.getLogger(JobController.class);

	@Autowired
	private JobService jobService;

	@Autowired
	private JobDtoValidator jobDtoValidator;

	@Autowired
	private JobDtoListValidator jobDtoListValidator;

	@InitBinder("jobDto")
	private void initBinder_jobDto(WebDataBinder binder) {
		binder.addValidators(jobDtoValidator);
	}

	@InitBinder("jobDtoList")
	private void initBinder_jobDtoList(WebDataBinder binder) {
		binder.addValidators(jobDtoListValidator);
	}

	@PostMapping("/{appName}")
	public JobDto createJob(@PathVariable String appName, @Valid @RequestBody JobDto jobDto, BindingResult result)
			throws JobStateManagerException {

		logger.debug("createJob: entering: appName={}, jobDto={}", appName, jobDto);

		if (result.hasErrors()) {
			throw new ValidationException(result.getFieldErrors());
		}

		return jobService.createJob(appName, jobDto);
	}

}
