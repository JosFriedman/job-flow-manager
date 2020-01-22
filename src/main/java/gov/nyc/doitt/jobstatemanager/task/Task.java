package gov.nyc.doitt.jobstatemanager.task;

import java.sql.Timestamp;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Task {

	@Id
	@GenericGenerator(name = "db-uuid", strategy = "guid")
	@GeneratedValue(generator = "db-uuid")
	private String _id;

	private String appId;
	private String jobId;

	private String description;
	private Timestamp createdTimestamp;
	@Enumerated(EnumType.STRING)
	private TaskState state;
	private Timestamp startTimestamp;
	private Timestamp endTimestamp;
	private String errorReason;

	public Task() {
		createdTimestamp = new Timestamp(System.currentTimeMillis());
		state = TaskState.NEW;
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

	public TaskState getState() {
		return state;
	}

	public Timestamp getStartTimestamp() {
		return startTimestamp;
	}

	public Timestamp getEndTimestamp() {
		return endTimestamp;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public void start() {
//		state = String.PROCESSING;
		startTimestamp = new Timestamp(System.currentTimeMillis());
	}

	public void endWithSuccess() {
		endTimestamp = new Timestamp(System.currentTimeMillis());
		this.state = TaskState.COMPLETED;
	}

	public void endWithError(String errorReason) {

		endTimestamp = new Timestamp(System.currentTimeMillis());
		this.state = TaskState.ERROR;
		this.errorReason = errorReason;
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
		Task other = (Task) obj;
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

}
