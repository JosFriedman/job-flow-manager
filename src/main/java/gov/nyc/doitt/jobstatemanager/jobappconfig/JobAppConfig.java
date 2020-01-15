package gov.nyc.doitt.jobstatemanager.jobappconfig;

import java.sql.Timestamp;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class JobAppConfig {

	@Id
	@GenericGenerator(name = "db-uuid", strategy = "guid")
	@GeneratedValue(generator = "db-uuid")
	private String _id;

	private String appId;
	private String description;
	private Timestamp createdTimestamp;
	private String notifyEmail;
	private int maxBatchSize;
	private int maxRetriesForError;

	public String getAppId() {
		return appId;
	}

	public String get_id() {
		return _id;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
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
		JobAppConfig other = (JobAppConfig) obj;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JobAppConfig [_id=" + _id + ", appId=" + appId + ", description=" + description + ", createdTimestamp="
				+ createdTimestamp + ", notifyEmail=" + notifyEmail + ", maxBatchSize=" + maxBatchSize + ", maxRetriesForError="
				+ maxRetriesForError + "]";
	}

}
