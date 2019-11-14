package gov.nyc.doitt.jobstatusmanager.domain.jobstatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import gov.nyc.doitt.jobstatusmanager.domain.jobstatus.dto.JobStatusDto;
import gov.nyc.doitt.jobstatusmanager.domain.jobstatus.model.JobStatus;

/**
 * Map JobStatus to and from JobStatusDto
 * 
 */
@Component
public class JobStatusDtoMapper {

	private ModelMapper modelMapper = new ModelMapper();

	private PropertyMap<JobStatusDto, JobStatus> jobStatusDtoPropertyMap = new PropertyMap<JobStatusDto, JobStatus>() {

		protected void configure() {
			skip(destination.getId());
		}
	};

	public JobStatusDtoMapper() {

		modelMapper.addMappings(jobStatusDtoPropertyMap);
//		TypeMap<JobStatusDto, JobStatus> jobStatusDtoTypeMap = modelMapper.createTypeMap(JobStatusDto.class,
//				JobStatus.class);
//		jobStatusDtoTypeMap.addMappings(p -> p.skip(JobStatus::setId));
//
//		TypeMap<JobStatus, JobStatusDto> jobStatusTypeMap = modelMapper.createTypeMap(JobStatus.class,
//				JobStatusDto.class);

	}

	public JobStatus fromDto(JobStatusDto jobStatusDto, JobStatus jobStatus) {

		modelMapper.map(jobStatusDto, jobStatus);
		return jobStatus;
	}

	public JobStatus fromDto(JobStatusDto jobStatusDto) {

		JobStatus jobStatus = modelMapper.map(jobStatusDto, JobStatus.class);
		return jobStatus;
	}

	public List<JobStatusDto> toDto(List<JobStatus> jobStatuses) {

		if (CollectionUtils.isEmpty(jobStatuses))
			return new ArrayList<JobStatusDto>();
		return jobStatuses.stream().map(p -> toDto(p)).collect(Collectors.toList());
	}

	public JobStatusDto toDto(JobStatus jobStatus) {

		JobStatusDto jobStatusDto = modelMapper.map(jobStatus, JobStatusDto.class);
		return jobStatusDto;
	}

}
