package gov.nyc.doitt.jobstatemanager.jobappconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Map JobAppConfig to and from JobAppConfigDto
 * 
 */
@Component
class JobAppConfigDtoMapper {

	private ModelMapper modelMapper = new ModelMapper();

	@Autowired
	private TaskConfigDtoMapper taskConfigDtoMapper;

	private PropertyMap<JobAppConfigDto, JobAppConfig> jobAppConfigDtoPropertyMap = new PropertyMap<JobAppConfigDto, JobAppConfig>() {

		protected void configure() {
			skip(destination.get_id());
			skip(destination.getCreatedTimestamp());
		}
	};

	public JobAppConfigDtoMapper() {

		modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE);
		modelMapper.addMappings(jobAppConfigDtoPropertyMap);
	}

	public JobAppConfig fromDto(JobAppConfigDto jobAppConfigDto) {

		JobAppConfig jobAppConfig = modelMapper.map(jobAppConfigDto, JobAppConfig.class);

		List<TaskConfig> taskConfigs = taskConfigDtoMapper.fromDto(jobAppConfigDto.getTaskConfigDtos());
		jobAppConfig.setTaskConfigs(taskConfigs);
		return jobAppConfig;
	}

	public JobAppConfig fromDto(JobAppConfigDto jobAppConfigDto, JobAppConfig jobAppConfig) {

		modelMapper.map(jobAppConfigDto, jobAppConfig);
		return jobAppConfig;
	}

	public List<JobAppConfigDto> toDto(List<JobAppConfig> jobAppConfigs) {

		if (CollectionUtils.isEmpty(jobAppConfigs))
			return new ArrayList<JobAppConfigDto>();
		return jobAppConfigs.stream().map(p -> toDto(p)).collect(Collectors.toList());
	}

	public JobAppConfigDto toDto(JobAppConfig jobAppConfig) {

		JobAppConfigDto jobAppConfigDto = modelMapper.map(jobAppConfig, JobAppConfigDto.class);

		List<TaskConfigDto> taskConfigDtos = taskConfigDtoMapper.toDto(jobAppConfig.getTaskConfigs());
		jobAppConfigDto.setTaskConfigDtos(taskConfigDtos);
		return jobAppConfigDto;

	}

}
