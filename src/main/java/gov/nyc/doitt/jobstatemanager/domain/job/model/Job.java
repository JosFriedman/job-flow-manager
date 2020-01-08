package gov.nyc.doitt.jobstatemanager.domain.job.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Job {

	@Id
	@GenericGenerator(name = "db-uuid", strategy = "guid")
	@GeneratedValue(generator = "db-uuid")
	@Column(name = "ID")
	private String _id;

	@Column(name = "APP_ID")
	private String appId;

	@Column(name = "JOB_ID")
	private String jobId;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "JOB_CREATED_TIMESTAMP")
	private Timestamp createdTimestamp = new Timestamp(System.currentTimeMillis());

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private JobState state;

	@Column(name = "START_TIMESTAMP")
	private Timestamp startTimestamp;

	@Column(name = "END_TIMESTAMP")
	private Timestamp endTimestamp;

	@Column(name = "ERROR_COUNT")
	private int errorCount;

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

	public void setState(JobState state) {
		this.state = state;
	}

	public void setStatusSmartly(JobState state) {
		this.state = state;
		endTimestamp = new Timestamp(System.currentTimeMillis());
		if (this.state == JobState.ERROR) {
			errorCount++;
		}
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

	public int incrementErrorCount() {
		return errorCount++;
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

}