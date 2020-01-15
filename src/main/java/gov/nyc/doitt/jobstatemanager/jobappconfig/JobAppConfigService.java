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

		String appId = jobAppConfigDto.getAppId();
		if (jobAppConfigRepository.existsByAppId(appId)) {
			throw new ConflictException(String.format("JobAppConfig for appId=%s already exists", appId));
		}

		JobAppConfig jobAppConfig = jobAppConfigDtoMapper.fromDto(jobAppConfigDto);
		jobAppConfigRepository.save(jobAppConfig);
		return jobAppConfigDtoMapper.toDto(jobAppConfig);
	}

	/**
	 * Get jobAppConfig for appId
	 * 
	 * @param appId
	 * @return
	 */
	public JobAppConfigDto getJobAppConfig(String appId) {

		return jobAppConfigDtoMapper.toDto(getJobAppConfigDomain(appId));
	}

	public JobAppConfig getJobAppConfigDomain(String appId) {
		if (!jobAppConfigRepository.existsByAppId(appId)) {
			throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appId=%s", appId));
		}
		return jobAppConfigRepository.findByAppId(appId);
	}

	public boolean existsJobAppConfig(String appId) {
		return jobAppConfigRepository.existsByAppId(appId);
	}

	/**
	 * Get all jobAppConfigs
	 * 
	 * @return
	 */
	public List<JobAppConfigDto> getJobAppConfigs() {

		return jobAppConfigDtoMapper.toDto(jobAppConfigRepository.findAllByOrderByAppIdAsc());
	}

	/**
	 * Update jobAppConfig specified by appIdd, from jobAppConfigDto
	 * 
	 * @param appId
	 * @param jobAppConfigDto
	 * @return
	 */
	public JobAppConfigDto updateJobAppConfig(String appId, JobAppConfigDto jobAppConfigDto) {

		if (!jobAppConfigRepository.existsByAppId(appId)) {
			throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appId=%s", appId));
		}

		JobAppConfig jobAppConfig = jobAppConfigRepository.findByAppId(appId);
		jobAppConfigDtoMapper.fromDto(jobAppConfigDto, jobAppConfig);
		jobAppConfigRepository.save(jobAppConfig);
		return jobAppConfigDtoMapper.toDto(jobAppConfig);
	}

	/**
	 * Delete jobAppConfig specified by appId
	 * 
	 * @param appId
	 * @return
	 */
	public String deleteJobAppConfig(String appId) {

		if (!jobAppConfigRepository.existsByAppId(appId)) {
			throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appId=%s", appId));
		}
		return jobAppConfigRepository.deleteByAppId(appId);
	}

}
