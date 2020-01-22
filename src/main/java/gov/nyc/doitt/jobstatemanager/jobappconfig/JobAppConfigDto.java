package gov.nyc.doitt.jobstatemanager.jobappconfig;

import java.sql.Timestamp;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class JobAppConfigDto {

	@NotBlank(message = "appId may not be empty")
	private String appId;
	private String description;
	private Timestamp createdTimestamp;
	private String notifyEmail;
	private int maxBatchSize;
	private int maxRetriesForError;

	private List<TaskConfigDto> taskConfigDtos;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
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

	public int getMaxBatchSize() {
		return maxBatchSize;
	}

	public void setMaxBatchSize(int maxBatchSize) {
		this.maxBatchSize = maxBatchSize;
	}

	public int getMaxRetriesForError() {
		return maxRetriesForError;
	}

	public void setMaxRetriesForError(int maxRetriesForError) {
		this.maxRetriesForError = maxRetriesForError;
	}

	public List<TaskConfigDto> getTaskConfigDtos() {
		return taskConfigDtos;
	}

	public void setTaskConfigDtos(List<TaskConfigDto> taskConfigDtos) {
		this.taskConfigDtos = taskConfigDtos;
	}

}
