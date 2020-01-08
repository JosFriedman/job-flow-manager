package gov.nyc.doitt.jobstatemanager.domain.job;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import gov.nyc.doitt.jobstatemanager.domain.job.dto.JobDto;
import gov.nyc.doitt.jobstatemanager.domain.job.model.Job;
import gov.nyc.doitt.jobstatemanager.domain.job.model.JobState;

/**
 * Map Job to and from JobDto
 * 
 */
@Component
class JobDtoMapper {

	private ModelMapper modelMapper = new ModelMapper();

	private PropertyMap<JobDto, Job> jobDtoPropertyMap = new PropertyMap<JobDto, Job>() {

		protected void configure() {
			skip(destination.get_id());
			skip(destination.getCreatedTimestamp());
		}
	};

	public JobDtoMapper() {

		modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE);

		modelMapper.addMappings(jobDtoPropertyMap);
	}

	public Job fromDto(JobDto jobDto, Job job) {

		modelMapper.map(jobDto, job);
		return job;
	}

	public Job fromDtoPatch(JobDto jobDto, Job job) {

		// Note: only field supported by patching
		job.setStatusSmartly(JobState.valueOf(jobDto.getState()));
		return job;
	}

	public Job fromDto(JobDto jobDto) {

		Job job = new Job();
		modelMapper.map(jobDto, job);
		return job;
	}

	public List<JobDto> toDto(List<Job> jobs) {

		if (CollectionUtils.isEmpty(jobs))
			return new ArrayList<JobDto>();
		return jobs.stream().map(p -> toDto(p)).collect(Collectors.toList());
	}

	public JobDto toDto(Job job) {

		JobDto jobDto = modelMapper.map(job, JobDto.class);
		return jobDto;
	}

}
