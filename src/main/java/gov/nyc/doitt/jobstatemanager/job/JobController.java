package gov.nyc.doitt.jobstatemanager.job;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

import gov.nyc.doitt.jobstatemanager.common.JobStateManagerException;
import gov.nyc.doitt.jobstatemanager.common.SortParamMapper;
import gov.nyc.doitt.jobstatemanager.common.ValidationException;

@RestController
@RequestMapping("jobStateManager")
public class JobController {

	private Logger logger = LoggerFactory.getLogger(JobController.class);

	@Autowired
	private JobService jobService;

	@PostMapping("/jobs")
	public JobDto createJob(@Valid @RequestBody JobDto jobDto) throws JobStateManagerException {

		logger.debug("createJob: entering: {}", jobDto);

		return jobService.createJob(jobDto);
	}

	@GetMapping("/jobs/{appId}")
	public List<JobDto> getJobs(@PathVariable String appId, @RequestParam(defaultValue = "false") boolean nextBatch,
			@RequestParam(required = false) String state, @RequestParam(name = "sort", required = false) String[] sortParams) {

		logger.debug("getJob: entering: appId={}, nextBatch={}, state={}, sortParams={}", appId, nextBatch, state, sortParams);

		if (nextBatch && (!StringUtils.isBlank(state) || !ArrayUtils.isEmpty(sortParams))) {
			throw new ValidationException("nextBatch and (state or sortParams) cannot be both specified");
		}
		if (nextBatch) {
			return jobService.getNextBatch(appId);
		}
		
		Sort sort = SortParamMapper.getSort(sortParams, "createdTimeStamp", Sort.Direction.DESC);
		if (!StringUtils.isBlank(state)) {
			return jobService.getJobs(appId, JobState.valueOf(state), sort);
		}
		return jobService.getJobs(appId, sort);
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
	public JobDto updateJob(@PathVariable String appId, @PathVariable String jobId, @Valid @RequestBody JobDto jobDto)
			throws JobStateManagerException {

		logger.debug("updateJob: entering: appId={}, jobId={}, jobDto={}", appId, jobId, jobDto);

		return jobService.updateJob(appId, jobId, jobDto);
	}

	@PatchMapping("/jobs/{appId}")
	public List<JobDto> patchJobs(@PathVariable String appId, @RequestBody List<JobDto> jobDtos) {

		if (logger.isDebugEnabled()) {
			logger.debug("patchJobs: entering: appId={}", appId);
			if (logger.isDebugEnabled()) {
				jobDtos.forEach(p -> logger.debug("job: {}", p));
			}
		}

		return jobService.updateJobsWithResults(appId, jobDtos);
	}

	@DeleteMapping("/jobs/{appId}/job/{jobId}")
	public String deleteJob(@PathVariable String appId, @PathVariable String jobId) {

		logger.debug("deleteJob: entering: appId={}, jobId={}", appId, jobId);

		return jobService.deleteJob(appId, jobId);
	}

}
