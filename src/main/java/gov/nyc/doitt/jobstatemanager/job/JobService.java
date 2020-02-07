package gov.nyc.doitt.jobstatemanager.job;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import gov.nyc.doitt.jobstatemanager.common.ConflictException;
import gov.nyc.doitt.jobstatemanager.common.EntityNotFoundException;
import gov.nyc.doitt.jobstatemanager.common.JobStateManagerException;
import gov.nyc.doitt.jobstatemanager.common.ValidationException;
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfig;
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfigService;
import gov.nyc.doitt.jobstatemanager.jobconfig.TaskConfig;

@Component
public class JobService {

	private Logger logger = LoggerFactory.getLogger(JobService.class);

	@Autowired
	private JobConfigService jobConfigService;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobDtoMapper jobDtoMapper;

	/**
	 * Create job from jobDto
	 * 
	 * @param jobDto
	 * @return
	 */
	@Transactional
	public JobDto createJob(String jobName, JobDto jobDto) {

		if (!jobConfigService.existsJobConfig(jobName)) {
			throw new EntityNotFoundException(String.format("Can't find JobConfig for jobName=%s", jobName));
		}
		String jobId = jobDto.getJobId();
		if (jobRepository.existsByJobNameAndJobId(jobName, jobId)) {
			throw new ConflictException(String.format("Job for jobName=%s, jobId=%s already exists", jobName, jobId));
		}

		Job job = jobDtoMapper.fromDto(jobName, jobDto);

		JobConfig jobConfig = jobConfigService.getJobConfigDomain(jobName);
		TaskConfig taskConfig = jobConfig.getTaskConfigs().stream().findFirst()
				.orElseThrow(() -> new JobStateManagerException("JobConfig for jobName=" + jobName + " has no TaskConfigs"));
		job.setNextTaskName(taskConfig.getName());
		jobRepository.save(job);
		return jobDtoMapper.toDto(job);
	}

	/**
	 * Get jobs for jobName
	 * 
	 * @param jobName
	 * @return
	 */
	List<JobDto> getJobs(String jobName, Sort sort) {

		return jobDtoMapper.toDto(jobRepository.findByJobName(jobName, sort));
	}

	/**
	 * Get jobs for jobName and state
	 * 
	 * @param jobName
	 * @return
	 */
	List<JobDto> getJobs(String jobName, JobState state, Sort sort) {

		return jobDtoMapper.toDto(jobRepository.findByJobNameAndState(jobName, state.name(), sort));
	}

	/**
	 * Delete job specified by jobName and jobId
	 * 
	 * @param jobName
	 * @param jobId
	 * @return
	 */
	public String deleteJob(String jobName, String jobId) {

		if (!jobRepository.existsByJobNameAndJobId(jobName, jobId)) {
			throw new EntityNotFoundException(String.format("Can't find Job for jobName=%s, jobId=%s", jobName, jobId));
		}
		jobRepository.deleteByJobNameAndJobId(jobName, jobId);
		return jobName + "/" + jobId;
	}

	/**
	 * Get all jobs
	 * 
	 * @return
	 */
	public List<JobDto> getJobs(Sort sort) {

		return jobDtoMapper.toDto(jobRepository.findAll(sort));
	}

	/**
	 * Get job specified by jobName and jobId
	 * 
	 * @param jobName
	 * @param jobId
	 * @return
	 */
	public JobDto getJob(String jobName, String jobId) {

		return jobDtoMapper.toDto(getJobDomain(jobName, jobId));
	}

	/**
	 * Get job specified by jobName and jobId
	 * 
	 * @param jobName
	 * @param jobId
	 * @return
	 */
	public Job getJobDomain(String jobName, String jobId) {

		Job job = jobRepository.findByJobNameAndJobId(jobName, jobId);
		if (job == null) {
			throw new EntityNotFoundException(String.format("Can't find Job for jobName=%s, jobId=%s", jobName, jobId));
		}
		return job;
	}

	@Transactional
	public JobDto patchJob(String jobName, String jobId, JobDto jobDto) {

		Job job = getJobDomain(jobName, jobId);

		JobState jobState;
		try {
			jobState = JobState.valueOf(jobDto.getState());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new ValidationException(
					String.format("Unsupported JobState=%s for patching jobName=%s, jobId=%s", jobDto.getState(), jobName, jobId));
		}
		job.setState(jobState);
		jobRepository.save(job);
		return jobDtoMapper.toDto(job);
	}

}
