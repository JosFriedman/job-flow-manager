package gov.nyc.doitt.jobstatemanager.jobconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import gov.nyc.doitt.jobstatemanager.security.Encryptor;

/**
 * Map JobConfig to and from JobConfigDto
 * 
 */
@Component
class JobConfigDtoMapper {

	private ModelMapper modelMapper = new ModelMapper();

	@Autowired
	private TaskConfigDtoMapper taskConfigDtoMapper;

	@Autowired
	private Encryptor encryptor;

	private Converter<String, String> encryptingConverter = new AbstractConverter<String, String>() {

		@Override
		protected String convert(String source) {
			return encryptor.encrypt(source);
		}
	};

	private Converter<String, String> decryptingConverter = new AbstractConverter<String, String>() {

		@Override
		protected String convert(String source) {
			return encryptor.decrypt(source);
		}
	};

	private PropertyMap<JobConfigDto, JobConfig> jobConfigDtoPropertyMap = new PropertyMap<JobConfigDto, JobConfig>() {

		protected void configure() {
			skip(destination.get_id());
			skip(destination.getCreatedTimestamp());
			using(encryptingConverter).map().setAuthToken(source.getAuthToken());
		}
	};

	private PropertyMap<JobConfig, JobConfigDto> jobConfigPropertyMap = new PropertyMap<JobConfig, JobConfigDto>() {

		protected void configure() {
			using(decryptingConverter).map().setAuthToken(source.getAuthToken());
		}
	};

	public JobConfigDtoMapper() {

		modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE);
		modelMapper.addMappings(jobConfigDtoPropertyMap);
		modelMapper.addMappings(jobConfigPropertyMap);
	}

	public JobConfig fromDto(JobConfigDto jobConfigDto) {

		JobConfig jobConfig = modelMapper.map(jobConfigDto, JobConfig.class);

		return fromDto(jobConfigDto, jobConfig);
	}

	public JobConfig fromDto(JobConfigDto jobConfigDto, JobConfig jobConfig) {

		modelMapper.map(jobConfigDto, jobConfig);

		ArrayList<TaskConfig> taskConfigs = taskConfigDtoMapper.fromDto(jobConfigDto.getTaskConfigDtos());
		jobConfig.setTaskConfigs(taskConfigs);

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
