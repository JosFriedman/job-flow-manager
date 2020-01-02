package gov.nyc.doitt.jobstatemanager.domain.jobstate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import gov.nyc.doitt.jobstatemanager.domain.jobstate.dto.JobStateDto;
import gov.nyc.doitt.jobstatemanager.domain.jobstate.model.JobState;
import gov.nyc.doitt.jobstatemanager.domain.jobstate.model.JobStatus;

/**
 * Map JobFlow to and from JobFlowDto
 * 
 */
@Component
class JobStateDtoMapper {

	private ModelMapper modelMapper = new ModelMapper();

	private PropertyMap<JobStateDto, JobState> jobStateDtoPropertyMap = new PropertyMap<JobStateDto, JobState>() {

		protected void configure() {
			skip(destination.get_id());
			skip(destination.getJobCreatedTimestamp());
		}
	};

	public JobStateDtoMapper() {

		modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE);

		modelMapper.addMappings(jobStateDtoPropertyMap);
	}

	public JobState fromDto(JobStateDto jobStateDto, JobState jobState) {

		modelMapper.map(jobStateDto, jobState);
		return jobState;
	}

	public JobState fromDtoPatch(JobStateDto jobStateDto, JobState jobState) {

		// Note: only field supported by patching
		jobState.setStatusSmartly(JobStatus.valueOf(jobStateDto.getStatus()));
		return jobState;
	}

	public JobState fromDto(JobStateDto jobStateDto) {

		JobState jobState = new JobState();
		modelMapper.map(jobStateDto, jobState);
		return jobState;
	}

	public List<JobStateDto> toDto(List<JobState> jobStates) {

		if (CollectionUtils.isEmpty(jobStates))
			return new ArrayList<JobStateDto>();
		return jobStates.stream().map(p -> toDto(p)).collect(Collectors.toList());
	}

	public JobStateDto toDto(JobState jobState) {

		JobStateDto jobStateDto = modelMapper.map(jobState, JobStateDto.class);
		return jobStateDto;
	}

}
