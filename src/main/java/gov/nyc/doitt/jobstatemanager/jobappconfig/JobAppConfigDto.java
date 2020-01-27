package gov.nyc.doitt.jobstatemanager.jobappconfig;

import java.sql.Timestamp;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class JobAppConfigDto {

	private String appName;
	private String description;
	private Timestamp createdTimestamp;
	private String notifyEmail;

	private List<TaskConfigDto> taskConfigDtos;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
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

	public List<TaskConfigDto> getTaskConfigDtos() {
		return taskConfigDtos;
	}

	public void setTaskConfigDtos(List<TaskConfigDto> taskConfigDtos) {
		this.taskConfigDtos = taskConfigDtos;
	}

	@Override
	public String toString() {
		return "JobAppConfigDto [appName=" + appName + ", description=" + description + ", createdTimestamp=" + createdTimestamp
				+ ", notifyEmail=" + notifyEmail + ", taskConfigDtos=" + taskConfigDtos + "]";
	}

}
