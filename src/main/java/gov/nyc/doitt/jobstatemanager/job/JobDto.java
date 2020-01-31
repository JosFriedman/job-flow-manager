package gov.nyc.doitt.jobstatemanager.job;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import gov.nyc.doitt.jobstatemanager.task.TaskDto;

@JsonInclude(Include.NON_NULL)
public class JobDto {

	private String jobName;
	private String jobId;
	private String description;
	private Timestamp createdTimestamp;
	private String state;
	private String nextTaskName;

	private ArrayList<TaskDto> taskDtos = new ArrayList<>();

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Timestamp createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getNextTaskName() {
		return nextTaskName;
	}

	public void setNextTaskName(String nextTaskName) {
		this.nextTaskName = nextTaskName;
	}

	public ArrayList<TaskDto> getTaskDtos() {
		return taskDtos;
	}

	public void setTaskDtos(ArrayList<TaskDto> taskDtos) {
		this.taskDtos = taskDtos;
	}

	@Override
	public String toString() {
		return "JobDto [jobName=" + jobName + ", jobId=" + jobId + ", description=" + description + ", createdTimestamp="
				+ createdTimestamp + ", state=" + state + ", nextTaskName=" + nextTaskName + ", taskDtos=" + taskDtos + "]";
	}

}
