package gov.nyc.doitt.jobstatemanager.jobappconfig;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

import gov.nyc.doitt.jobstatemanager.common.JobStateManagerException;

@Document
public class JobAppConfig {

	@Id
	@GenericGenerator(name = "db-uuid", strategy = "guid")
	@GeneratedValue(generator = "db-uuid")
	private String _id;

	private String appName;
	private String description;
	private Timestamp createdTimestamp;
	private String notifyEmail;

	private List<TaskConfig> taskConfigs;

	public JobAppConfig() {
		createdTimestamp = new Timestamp(System.currentTimeMillis());
	}

	public String getAppName() {
		return appName;
	}

	public String get_id() {
		return _id;
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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appName == null) ? 0 : appName.hashCode());
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
		if (appName == null) {
			if (other.appName != null)
				return false;
		} else if (!appName.equals(other.appName))
			return false;
		return true;
	}

	public List<TaskConfig> getTaskConfigs() {
		return taskConfigs;
	}

	public void setTaskConfigs(List<TaskConfig> taskConfigs) {
		this.taskConfigs = taskConfigs;
	}

	public TaskConfig getTaskConfig(String taskName) {
		
		return taskConfigs.stream().filter(p -> p.getName().equals(taskName)).findFirst().orElseThrow(() -> new JobStateManagerException("TaskConfig for taskName=" + taskName + " not found"));
	}
	
	@Override
	public String toString() {
		return "JobAppConfig [_id=" + _id + ", appName=" + appName + ", description=" + description + ", createdTimestamp="
				+ createdTimestamp + ", notifyEmail=" + notifyEmail + ", taskConfigs=" + taskConfigs + "]";
	}

}
