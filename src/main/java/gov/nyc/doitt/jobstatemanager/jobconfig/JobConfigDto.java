package gov.nyc.doitt.jobstatemanager.jobconfig;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class JobConfigDto {

	private String jobName;
	private String description;
	private Timestamp createdTimestamp;
	private String notifyEmail;
	private String authToken;

	private ArrayList<TaskConfigDto> taskConfigDtos;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
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

	public String getNotifyEmail() {
		return notifyEmail;
	}

	public void setNotifyEmail(String notifyEmail) {
		this.notifyEmail = notifyEmail;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public ArrayList<TaskConfigDto> getTaskConfigDtos() {
		return taskConfigDtos;
	}

	public void setTaskConfigDtos(ArrayList<TaskConfigDto> taskConfigDtos) {
		this.taskConfigDtos = taskConfigDtos;
	}

	@Override
	public String toString() {
		return "JobConfigDto [jobName=" + jobName + ", description=" + description + ", createdTimestamp=" + createdTimestamp
				+ ", notifyEmail=" + notifyEmail + ", taskConfigDtos=" + taskConfigDtos + "]";
	}

}
