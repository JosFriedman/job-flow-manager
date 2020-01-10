package gov.nyc.doitt.jobstatemanager.domain.jobappconfig;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nyc.doitt.jobstatemanager.domain.jobappconfig.dto.JobAppConfigDto;
import gov.nyc.doitt.jobstatemanager.domain.jobappconfig.model.JobAppConfig;

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
	JobAppConfigDto getJobAppConfig(String appId) {

		try {
			return jobAppConfigDtoMapper.toDto(jobAppConfigRepository.findByAppId(appId));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get all jobAppConfigs
	 * 
	 * @return
	 */
	public List<JobAppConfigDto> getJobAppConfigs() {

		return jobAppConfigDtoMapper.toDto(jobAppConfigRepository.findAll());
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
			throw new EntityNotFoundException(String.format("Can't find JobAppConfig for appId=%s, jobId=%s", appId));
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
