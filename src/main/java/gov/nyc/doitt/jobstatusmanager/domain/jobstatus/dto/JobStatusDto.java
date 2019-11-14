package gov.nyc.doitt.jobstatusmanager.domain.jobstatus.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class JobStatusDto {

	private String appId;
	private String jobId;
	private String description;
	private Timestamp jobCreated;
	private String status;
	private Timestamp startTimestamp;
	private Timestamp endTimestamp;
	private int errorCount;

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

	public Timestamp getJobCreated() {
		return jobCreated;
	}

	public void setJobCreated(Timestamp jobCreated) {
		this.jobCreated = jobCreated;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	@Override
	public String toString() {
		return "JobStatusDto [appId=" + appId + ", jobId=" + jobId + ", description=" + description + ", jobCreated="
				+ jobCreated + ", status=" + status + ", startTimestamp=" + startTimestamp + ", endTimestamp="
				+ endTimestamp + ", errorCount=" + errorCount + "]";
	}

}