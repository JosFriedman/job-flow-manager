package gov.nyc.doitt.jobstatemanager.job;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.nyc.doitt.jobstatemanager.common.JobStateManagerException;
import gov.nyc.doitt.jobstatemanager.common.SortParamMapper;
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

	@PostMapping(params = { "jobName" })
	public JobDto createJob(@RequestParam String jobName, @Valid @RequestBody JobDto jobDto, BindingResult result)
			throws JobStateManagerException {

		logger.debug("createJob: entering: jobName={}, jobDto={}", jobName, jobDto);

		if (result.hasErrors()) {
			throw new ValidationException(result.getFieldErrors());
		}

		return jobService.createJob(jobName, jobDto);
	}

	@GetMapping(params = { "jobName" })
	public List<JobDto> getJobs(@RequestParam String jobName, @RequestParam(required = false) String state,
			@RequestParam(name = "sort", required = false) String[] sortParams) {

		logger.debug("getJob: entering: jobName={}, state={}, sortParams={}", jobName, state, sortParams);

		Sort sort = SortParamMapper.getSort(sortParams, "createdTimeStamp", Sort.Direction.DESC);
		if (!StringUtils.isBlank(state)) {
			return jobService.getJobs(jobName, JobState.valueOf(state), sort);
		}
		return jobService.getJobs(jobName, sort);
	}

	@GetMapping
	public List<JobDto> getJobs(@RequestParam(name = "sort", required = false) String[] sortParams) {

		logger.debug("getJob: entering: sortParams={}", (Object[]) sortParams);

		Sort sort = SortParamMapper.getSort(sortParams, "createdTimeStamp", Sort.Direction.DESC);

		return jobService.getJobs(sort);
	}

	@GetMapping(params = { "jobName", "jobId" })
	public JobDto getJob(@RequestParam String jobName, @RequestParam String jobId) {

		logger.debug("getJob: entering: jobName={}, jobId={}", jobName, jobId);

		return jobService.getJob(jobName, jobId);
	}

	@PatchMapping(params = { "jobName", "jobId" })
	public JobDto resetJob(@RequestParam String jobName, @RequestParam String jobId) throws JobStateManagerException {

		logger.debug("resetJob: entering: jobName={}, jobId={}", jobName);

		return jobService.resetJob(jobName, jobId);
	}

	@DeleteMapping(params = { "jobName", "jobId" })
	public String deleteJob(@RequestParam String jobName, @RequestParam String jobId) {

		logger.debug("deleteJob: entering: jobName={}, jobId={}", jobName, jobId);

		return jobService.deleteJob(jobName, jobId);
	}

}
