package gov.nyc.doitt.jobstatemanager.jobconfig;

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
 * Map JobConfig to and from JobConfigDto
 * 
 */
@Component
class JobConfigDtoMapper {

	private ModelMapper modelMapper = new ModelMapper();

	@Autowired
	private TaskConfigDtoMapper taskConfigDtoMapper;

	private PropertyMap<JobConfigDto, JobConfig> jobConfigDtoPropertyMap = new PropertyMap<JobConfigDto, JobConfig>() {

		protected void configure() {
			skip(destination.get_id());
			skip(destination.getCreatedTimestamp());
		}
	};

	public JobConfigDtoMapper() {

		modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE);
		modelMapper.addMappings(jobConfigDtoPropertyMap);
	}

	public JobConfig fromDto(JobConfigDto jobConfigDto) {

		JobConfig jobConfig = modelMapper.map(jobConfigDto, JobConfig.class);

		ArrayList<TaskConfig> taskConfigs = taskConfigDtoMapper.fromDto(jobConfigDto.getTaskConfigDtos());
		jobConfig.setTaskConfigs(taskConfigs);
		return jobConfig;
	}

	public JobConfig fromDto(JobConfigDto jobConfigDto, JobConfig jobConfig) {

		modelMapper.map(jobConfigDto, jobConfig);
		return jobConfig;
	}

	public List<JobConfigDto> toDto(List<JobConfig> jobConfigs) {

		if (CollectionUtils.isEmpty(jobConfigs))
			return new ArrayList<JobConfigDto>();
		return jobConfigs.stream().map(p -> toDto(p)).collect(Collectors.toList());
	}

	public JobConfigDto toDto(JobConfig jobConfig) {

		JobConfigDto jobConfigDto = modelMapper.map(jobConfig, JobConfigDto.class);

		ArrayList<TaskConfigDto> taskConfigDtos = taskConfigDtoMapper.toDto(jobConfig.getTaskConfigs());
		jobConfigDto.setTaskConfigDtos(taskConfigDtos);
		return jobConfigDto;

	}

}
