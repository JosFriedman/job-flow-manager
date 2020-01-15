package gov.nyc.doitt.jobstatemanager.job;

import java.sql.Timestamp;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class JobDto {

	@NotBlank(message = "appId may not be empty")
	private String appId;
	@NotBlank(message = "jobId may not be empty")
	private String jobId;
	private String description;
	private Timestamp createdTimestamp;
	private String state;
	private String payload;
	private Timestamp startTimestamp;
	private Timestamp endTimestamp;
	private int errorCount;
	private String errorReason;
	private Boolean reset;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
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

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Timestamp getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(Timestamp startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public Timestamp getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(Timestamp endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}

	public Boolean isReset() {
		return reset;
	}

	public void setReset(Boolean reset) {
		this.reset = reset;
	}

	@Override
	public String toString() {
		return "JobDto [appId=" + appId + ", jobId=" + jobId + ", description=" + description + ", createdTimestamp="
				+ createdTimestamp + ", state=" + state + ", payload=" + payload + ", startTimestamp=" + startTimestamp
				+ ", endTimestamp=" + endTimestamp + ", errorCount=" + errorCount + ", errorReason=" + errorReason + ", reset="
				+ reset + "]";
	}

}
