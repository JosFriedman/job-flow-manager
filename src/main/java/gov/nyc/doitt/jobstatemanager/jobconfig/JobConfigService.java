package gov.nyc.doitt.jobstatemanager.jobconfig;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nyc.doitt.jobstatemanager.common.ConflictException;
import gov.nyc.doitt.jobstatemanager.common.EntityNotFoundException;

@Component
public class JobConfigService {

	private Logger logger = LoggerFactory.getLogger(JobConfigService.class);

	@Autowired
	private JobConfigRepository jobConfigRepository;

	@Autowired
	private JobConfigDtoMapper jobConfigDtoMapper;

	/**
	 * Create jobConfig from jobConfigDto
	 * 
	 * @param jobConfigDto
	 * @return
	 */
	@Transactional
	public JobConfigDto createJobConfig(JobConfigDto jobConfigDto) {

		String jobName = jobConfigDto.getJobName();
		if (jobConfigRepository.existsByJobName(jobName)) {
			throw new ConflictException(String.format("JobConfig for jobName=%s already exists", jobName));
		}

		JobConfig jobConfig = jobConfigDtoMapper.fromDto(jobConfigDto);
		jobConfigRepository.save(jobConfig);
		return jobConfigDtoMapper.toDto(jobConfig);
	}

	/**
	 * Get jobConfig for jobName
	 * 
	 * @param jobName
	 * @return
	 */
	public JobConfigDto getJobConfig(String jobName) {

		return jobConfigDtoMapper.toDto(getJobConfigDomain(jobName));
	}

	public JobConfig getJobConfigDomain(String jobName) {
		if (!jobConfigRepository.existsByJobName(jobName)) {
			throw new EntityNotFoundException(String.format("Can't find JobConfig for jobName=%s", jobName));
		}
		return jobConfigRepository.findByJobName(jobName);
	}

	public boolean existsJobConfig(String jobName) {
		return jobConfigRepository.existsByJobName(jobName);
	}

	/**
	 * Get all jobConfigs
	 * 
	 * @return
	 */
	public List<JobConfigDto> getJobConfigs() {

		return jobConfigDtoMapper.toDto(jobConfigRepository.findAllByOrderByJobNameAsc());
	}

	/**
	 * Update jobConfig specified by jobNamed, from jobConfigDto
	 * 
	 * @param jobName
	 * @param jobConfigDto
	 * @return
	 */
	@Transactional
	public JobConfigDto updateJobConfig(String jobName, JobConfigDto jobConfigDto) {

		if (!jobConfigRepository.existsByJobName(jobName)) {
			throw new EntityNotFoundException(String.format("Can't find JobConfig for jobName=%s", jobName));
		}

		JobConfig jobConfig = jobConfigRepository.findByJobName(jobName);
		jobConfigDtoMapper.fromDto(jobConfigDto, jobConfig);
		jobConfigRepository.save(jobConfig);
		return jobConfigDtoMapper.toDto(jobConfig);
	}

	/**
	 * Delete jobConfig specified by jobName
	 * 
	 * @param jobName
	 * @return
	 */
	@Transactional
	public String deleteJobConfig(String jobName) {

		if (!jobConfigRepository.existsByJobName(jobName)) {
			throw new EntityNotFoundException(String.format("Can't find JobConfig for jobName=%s", jobName));
		}
		jobConfigRepository.deleteByJobName(jobName);
		return jobName;
	}

}
