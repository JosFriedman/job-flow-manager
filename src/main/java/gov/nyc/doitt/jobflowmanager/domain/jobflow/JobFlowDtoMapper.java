package gov.nyc.doitt.jobflowmanager.domain.jobflow;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import gov.nyc.doitt.jobflowmanager.domain.jobflow.dto.JobFlowDto;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobFlow;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobStatus;

/**
 * Map JobFlow to and from JobFlowDto
 * 
 */
@Component
class JobFlowDtoMapper {

	private ModelMapper modelMapper = new ModelMapper();

	private PropertyMap<JobFlowDto, JobFlow> jobFlowDtoPropertyMap = new PropertyMap<JobFlowDto, JobFlow>() {

		protected void configure() {
			skip(destination.getId());
			skip(destination.getJobCreatedTimestamp());
		}
	};

	public JobFlowDtoMapper() {

		modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE);

		modelMapper.addMappings(jobFlowDtoPropertyMap);
//		TypeMap<JobFlowDto, JobFlow> jobFlowDtoTypeMap = modelMapper.createTypeMap(JobFlowDto.class,
//				JobFlow.class);
//		jobFlowDtoTypeMap.addMappings(p -> p.skip(JobFlow::setId));
//
//		TypeMap<JobFlow, JobFlowDto> jobFlowTypeMap = modelMapper.createTypeMap(JobFlow.class,
//				JobFlowDto.class);

	}

	public JobFlow fromDto(JobFlowDto jobFlowDto, JobFlow jobFlow) {

		modelMapper.map(jobFlowDto, jobFlow);
		return jobFlow;
	}

	public JobFlow fromDtoPatch(JobFlowDto jobFlowDto, JobFlow jobFlow) {

		// Note: only field supported by patching
		jobFlow.setResultStatus(JobStatus.valueOf(jobFlowDto.getStatus()));
		return jobFlow;
	}

	public JobFlow fromDto(JobFlowDto jobFlowDto) {

		JobFlow jobFlow = new JobFlow();
		modelMapper.map(jobFlowDto, jobFlow);
		return jobFlow;
	}

	public List<JobFlowDto> toDto(List<JobFlow> jobFlows) {

		if (CollectionUtils.isEmpty(jobFlows))
			return new ArrayList<JobFlowDto>();
		return jobFlows.stream().map(p -> toDto(p)).collect(Collectors.toList());
	}

	public JobFlowDto toDto(JobFlow jobFlow) {

		JobFlowDto jobFlowDto = modelMapper.map(jobFlow, JobFlowDto.class);
		return jobFlowDto;
	}

}
