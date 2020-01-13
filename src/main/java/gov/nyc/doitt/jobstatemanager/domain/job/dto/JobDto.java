package gov.nyc.doitt.jobstatemanager.domain.job.dto;

import java.sql.Timestamp;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class JobDto {

	@NotEmpty(message = "appId may not be empty")
	private String appId;
	@NotEmpty(message = "jobId may not be empty")
	private String jobId;
	private String description;
	private Timestamp createdTimestamp;
	private String state;
	private Timestamp startTimestamp;
	private Timestamp endTimestamp;
	private int errorCount;
	private String errorReason;

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

	@Override
	public String toString() {
		return "JobDto [appId=" + appId + ", jobId=" + jobId + ", description=" + description + ", createdTimestamp="
				+ createdTimestamp + ", state=" + state + ", startTimestamp=" + startTimestamp + ", endTimestamp=" + endTimestamp
				+ ", errorCount=" + errorCount + ", errorReason=" + errorReason + "]";
	}

}
