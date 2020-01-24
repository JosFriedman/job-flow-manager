package gov.nyc.doitt.jobstatemanager.jobappconfig;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nyc.doitt.jobstatemanager.common.ConflictException;
import gov.nyc.doitt.jobstatemanager.common.EntityNotFoundException;

@Component
public class JobAppConfigService {

	private Logger logger = LoggerFactory.getLogger(JobAppConfigService.class);

	@Autowired
	private JobAppConfigRepository jobAppConfigRepository;

	@Autowired
	private JobAppConfigDtoMapper jobAppConfigDtoMapper;

	/**
	 * Create jobAppConfig from jobAppConfigDto
	 * 
	 * @param jobAppConfigDto
	 * @return
	 */
	public JobAppConfigDto createJobAppConfig(JobAppConfigDto jobAppConfigDto) {

		String appName = jobAppConfigDto.getAppName();
		if (jobAppConfigRepository.existsByAppName(appName)) {
			throw new ConflictException(String.format("JobAppConfig for appName=%s already exists", appName));
		}

		JobAppConfig jobAppConfig = jobAppConfigDtoMapper.fromDto(jobAppConfigDto);
		jobAppConfigRepository.save(jobAppConfig);
		return jobAppConfigDtoMapper.toDto(jobAppConfig);
	}

	/**
	 * Get jobAppConfig for appName
	 * 
	 * @param appName
	 * @return
	 */
	public JobAppConfigDto getJobAppConfig(String appName) {

		return jobAppConfigDtoMapper.toDto(getJobAppConfigDomain(appName));
	}

	public JobAppConfig getJobAppConfigDomain(String appName) {
		if (!jobAppConfigRepository.existsByAppName(appName)) {
			throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appName=%s", appName));
		}
		return jobAppConfigRepository.findByAppName(appName);
	}

	public boolean existsJobAppConfig(String appName) {
		return jobAppConfigRepository.existsByAppName(appName);
	}

	/**
	 * Get all jobAppConfigs
	 * 
	 * @return
	 */
	public List<JobAppConfigDto> getJobAppConfigs() {

		return jobAppConfigDtoMapper.toDto(jobAppConfigRepository.findAllByOrderByAppNameAsc());
	}

	/**
	 * Update jobAppConfig specified by appNamed, from jobAppConfigDto
	 * 
	 * @param appName
	 * @param jobAppConfigDto
	 * @return
	 */
	public JobAppConfigDto updateJobAppConfig(String appName, JobAppConfigDto jobAppConfigDto) {

		if (!jobAppConfigRepository.existsByAppName(appName)) {
			throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appName=%s", appName));
		}

		JobAppConfig jobAppConfig = jobAppConfigRepository.findByAppName(appName);
		jobAppConfigDtoMapper.fromDto(jobAppConfigDto, jobAppConfig);
		jobAppConfigRepository.save(jobAppConfig);
		return jobAppConfigDtoMapper.toDto(jobAppConfig);
	}

	/**
	 * Delete jobAppConfig specified by appName
	 * 
	 * @param appName
	 * @return
	 */
	public String deleteJobAppConfig(String appName) {

		if (!jobAppConfigRepository.existsByAppName(appName)) {
			throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appName=%s", appName));
		}
		jobAppConfigRepository.deleteByAppName(appName);
		return appName;
	}

}
