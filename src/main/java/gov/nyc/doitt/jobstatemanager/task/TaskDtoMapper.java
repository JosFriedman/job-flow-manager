package gov.nyc.doitt.jobstatemanager.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import gov.nyc.doitt.jobstatemanager.common.JobStateManagerException;

/**
 * Map Task to and from TaskDto
 * 
 */
@Component
public class TaskDtoMapper {

	private ModelMapper modelMapper = new ModelMapper();

	private PropertyMap<TaskDto, Task> taskDtoPropertyMap = new PropertyMap<TaskDto, Task>() {

		protected void configure() {
			skip(destination.getState());
		}
	};

	public TaskDtoMapper() {

		modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE);

		modelMapper.addMappings(taskDtoPropertyMap);
	}

	public Task fromDto(TaskDto taskDto) {

		return modelMapper.map(taskDto, Task.class);
	}

	public List<Task> fromDto(List<TaskDto> taskDtos) {

		if (CollectionUtils.isEmpty(taskDtos))
			return new ArrayList<Task>();
		return taskDtos.stream().map(p -> {
			Task task = fromDto(p);
			return task;
		}).collect(Collectors.toList());

	}

	public Task fromDto(TaskDto taskDto, Task task) {

		modelMapper.map(taskDto, task);
		return task;
	}

	public Task fromDtoResult(TaskDto taskDto, Task task) {

		TaskState state = TaskState.valueOf(taskDto.getState());
		if (state == TaskState.ERROR) {
			task.endWithError(taskDto.getErrorReason());
		} else if (state == TaskState.COMPLETED) {
			task.endWithSuccess();
		} else {
			throw new JobStateManagerException("Unsupported state for result: " + state);
		}
		return task;
	}

//	public List<TaskDto> toDto(List<Task> tasks) {
//
//		if (CollectionUtils.isEmpty(tasks))
//			return new ArrayList<TaskDto>();
//		return tasks.stream().map(p -> toDto(p)).collect(Collectors.toList());
//	}

	public TaskDto toDto(String jobId, Task task) {

		TaskDto taskDto =  modelMapper.map(task, TaskDto.class);
		taskDto.setJobId(jobId);
		return taskDto;
	}

}
