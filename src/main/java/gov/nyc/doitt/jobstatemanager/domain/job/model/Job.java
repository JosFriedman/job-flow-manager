package gov.nyc.doitt.jobstatemanager.domain.job.model;

import java.sql.Timestamp;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Job {

	@Id
	@GenericGenerator(name = "db-uuid", strategy = "guid")
	@GeneratedValue(generator = "db-uuid")
	private String _id;

	private String appId;
	private String jobId;
	private String description;
	private Timestamp createdTimestamp;
	private String payload;
	@Enumerated(EnumType.STRING)
	private JobState state;
	private Timestamp startTimestamp;
	private Timestamp endTimestamp;
	private int errorCount;
	private String errorReason;

	public Job() {
		createdTimestamp = new Timestamp(System.currentTimeMillis());
		state = JobState.NEW;
	}

	public String get_id() {
		return _id;
	}

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

	public JobState getState() {
		return state;
	}

	public String getPayload() {
		return payload;
	}

	public Timestamp getStartTimestamp() {
		return startTimestamp;
	}

	public Timestamp getEndTimestamp() {
		return endTimestamp;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public void start() {
		state = JobState.PROCESSING;
		startTimestamp = new Timestamp(System.currentTimeMillis());
	}

	public void endWithSuccess() {
		endTimestamp = new Timestamp(System.currentTimeMillis());
		this.state = JobState.COMPLETED;
	}

	public void endWithError(String errorReason) {

		endTimestamp = new Timestamp(System.currentTimeMillis());
		this.state = JobState.ERROR;
		this.errorReason = errorReason;
		errorCount++;
	}

	public void resetWithError() {

		endTimestamp = new Timestamp(System.currentTimeMillis());
		this.state = JobState.ERROR;
		this.errorReason = "Reset";
		errorCount = 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Job other = (Job) obj;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (jobId == null) {
			if (other.jobId != null)
				return false;
		} else if (!jobId.equals(other.jobId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Job [id=" + _id + ", appId=" + appId + ", jobId=" + jobId + ", description=" + description + ", createdTimestamp="
				+ createdTimestamp + ", state=" + state + ", startTimestamp=" + startTimestamp + ", endTimestamp=" + endTimestamp
				+ ", errorCount=" + errorCount + "]";
	}

	// TODO: josfriedman: remove these setters and change unit tests

	public void setState(JobState state) {
		this.state = state;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

}
