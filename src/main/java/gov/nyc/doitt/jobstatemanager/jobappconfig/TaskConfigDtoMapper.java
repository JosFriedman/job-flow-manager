package gov.nyc.doitt.jobstatemanager.jobappconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Map TaskConfig to and from TaskConfigDto
 * 
 */
@Component
public class TaskConfigDtoMapper {

	private ModelMapper modelMapper = new ModelMapper();

	private PropertyMap<TaskConfigDto, TaskConfig> taskConfigDtoPropertyMap = new PropertyMap<TaskConfigDto, TaskConfig>() {

		protected void configure() {
			skip(destination.get_id());
			skip(destination.getCreatedTimestamp());
		}
	};

	public TaskConfigDtoMapper() {

		modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE);

		modelMapper.addMappings(taskConfigDtoPropertyMap);
	}

	public TaskConfig fromDto(TaskConfigDto taskConfigDto) {

		return modelMapper.map(taskConfigDto, TaskConfig.class);
	}

	public List<TaskConfig> fromDto(List<TaskConfigDto> taskConfigDtos) {

		if (CollectionUtils.isEmpty(taskConfigDtos))
			return new ArrayList<TaskConfig>();
		return taskConfigDtos.stream().map(p -> {
			TaskConfig taskConfig = fromDto(p);
			return taskConfig;
		}).collect(Collectors.toList());

	}

	public TaskConfig fromDto(TaskConfigDto taskConfigDto, TaskConfig taskConfig) {

		modelMapper.map(taskConfigDto, taskConfig);
		return taskConfig;
	}

	public List<TaskConfigDto> toDto(List<TaskConfig> taskConfigs) {

		if (CollectionUtils.isEmpty(taskConfigs))
			return new ArrayList<TaskConfigDto>();
		return taskConfigs.stream().map(p -> toDto(p)).collect(Collectors.toList());
	}

	public TaskConfigDto toDto(TaskConfig taskConfig) {

		return modelMapper.map(taskConfig, TaskConfigDto.class);
	}

}
