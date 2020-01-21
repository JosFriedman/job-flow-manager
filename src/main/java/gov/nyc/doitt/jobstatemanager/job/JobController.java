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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.nyc.doitt.jobstatemanager.common.JobStateManagerException;
import gov.nyc.doitt.jobstatemanager.common.SortParamMapper;
import gov.nyc.doitt.jobstatemanager.common.ValidationException;

@RestController
@RequestMapping("jobStateManager")
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

	@PostMapping("/jobs")
	public JobDto createJob(@Valid @RequestBody JobDto jobDto, BindingResult result) throws JobStateManagerException {

		logger.debug("createJob: entering: {}", jobDto);

		if (result.hasErrors()) {
			throw new ValidationException(result.getFieldErrors());
		}

		return jobService.createJob(jobDto);
	}

	@GetMapping("/jobs/{appId}")
	public List<JobDto> getJobs(@PathVariable String appId, @RequestParam(required = false) String state,
			@RequestParam(name = "sort", required = false) String[] sortParams) {

		logger.debug("getJob: entering: appId={}, state={}, sortParams={}", appId, state, sortParams);

		Sort sort = SortParamMapper.getSort(sortParams, "createdTimeStamp", Sort.Direction.DESC);
		if (!StringUtils.isBlank(state)) {
			return jobService.getJobs(appId, JobState.valueOf(state), sort);
		}
		return jobService.getJobs(appId, sort);
	}

	@PostMapping("/jobs/{appId}/startNextBatch")
	public List<JobDto> startNextBatch(@PathVariable String appId) {

		logger.debug("startNextBatch: entering: appId={}", appId);
		return jobService.startNextBatch(appId);
	}

	@GetMapping("/jobs")
	public List<JobDto> getJobs(@RequestParam(name = "sort", required = false) String[] sortParams) {

		Sort sort = SortParamMapper.getSort(sortParams, "createdTimeStamp", Sort.Direction.DESC);

		return jobService.getJobs(sort);
	}

	@GetMapping("/jobs/{appId}/job/{jobId}")
	public JobDto getJob(@PathVariable String appId, @PathVariable String jobId) {

		logger.debug("getJob: entering: appId={}, jobId={}", appId, jobId);

		return jobService.getJob(appId, jobId);
	}

	@PutMapping("/jobs/{appId}/job/{jobId}")
	public JobDto updateJob(@PathVariable String appId, @PathVariable String jobId, @Valid @RequestBody JobDto jobDto,
			BindingResult result) throws JobStateManagerException {

		logger.debug("updateJob: entering: appId={}, jobId={}, jobDto={}", appId, jobId, jobDto);

		if (result.hasErrors()) {
			throw new ValidationException(result.getFieldErrors());
		}

		return jobService.updateJob(appId, jobId, jobDto);
	}

	@PatchMapping("/jobs/{appId}")
	public List<JobDto> patchJobs(@PathVariable String appId, @Valid @RequestBody List<JobDto> jobDtos, BindingResult result) {

		if (logger.isDebugEnabled()) {
			logger.debug("patchJobs: entering: appId={}", appId);
			if (logger.isDebugEnabled()) {
				jobDtos.forEach(p -> logger.debug("job: {}", p));
			}
		}

		if (result.hasErrors()) {
			throw new ValidationException(result.getFieldErrors());
		}
		return jobService.updateJobsWithResults(appId, jobDtos);
	}

	@DeleteMapping("/jobs/{appId}/job/{jobId}")
	public String deleteJob(@PathVariable String appId, @PathVariable String jobId) {

		logger.debug("deleteJob: entering: appId={}, jobId={}", appId, jobId);

		return jobService.deleteJob(appId, jobId);
	}

}
