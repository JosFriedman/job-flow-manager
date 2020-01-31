package gov.nyc.doitt.jobstatemanager.job;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import gov.nyc.doitt.jobstatemanager.task.TaskDto;
import gov.nyc.doitt.jobstatemanager.task.TaskDtoMapper;

/**
 * Map Job to and from JobDto
 * 
 */
@Component
class JobDtoMapper {

	@Autowired
	private TaskDtoMapper taskDtoMapper;

	private ModelMapper modelMapper = new ModelMapper();

	private PropertyMap<JobDto, Job> jobDtoPropertyMap = new PropertyMap<JobDto, Job>() {

		protected void configure() {
			skip(destination.get_id());
			skip(destination.getJobName());
			skip(destination.getCreatedTimestamp());
			skip(destination.getState());
			skip(destination.getTasks());
		}
	};

	public JobDtoMapper() {

		modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE);
		modelMapper.addMappings(jobDtoPropertyMap);
	}

	public Job fromDto(String jobName, JobDto jobDto) {

		Job job = modelMapper.map(jobDto, Job.class);
		job.setJobName(jobName);
		return job;
	}

	public Job fromDto(JobDto jobDto, Job job) {

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

		ArrayList<TaskDto> taskDtos = taskDtoMapper.toDto(job, job.getTasks());
		jobDto.setTaskDtos(taskDtos);

		return jobDto;

	}

}
