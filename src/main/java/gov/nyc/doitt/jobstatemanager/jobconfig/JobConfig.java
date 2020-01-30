package gov.nyc.doitt.jobstatemanager.jobconfig;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

import gov.nyc.doitt.jobstatemanager.common.JobStateManagerException;

@Document
public class JobConfig {

	@Id
	@GenericGenerator(name = "db-uuid", strategy = "guid")
	@GeneratedValue(generator = "db-uuid")
	private String _id;

	private String jobName;
	private String description;
	private Timestamp createdTimestamp;
	private String notifyEmail;

	private ArrayList<TaskConfig> taskConfigs;

	public JobConfig() {
		createdTimestamp = new Timestamp(System.currentTimeMillis());
	}

	public String getJobName() {
		return jobName;
	}

	public String get_id() {
		return _id;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
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
		result = prime * result + ((jobName == null) ? 0 : jobName.hashCode());
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
		JobConfig other = (JobConfig) obj;
		if (jobName == null) {
			if (other.jobName != null)
				return false;
		} else if (!jobName.equals(other.jobName))
			return false;
		return true;
	}

	public List<TaskConfig> getTaskConfigs() {
		return taskConfigs;
	}

	public void setTaskConfigs(ArrayList<TaskConfig> taskConfigs) {
		this.taskConfigs = taskConfigs;
	}

	public TaskConfig getTaskConfig(String taskName) {
		
		return taskConfigs.stream().filter(p -> p.getName().equals(taskName)).findFirst().orElseThrow(() -> new JobStateManagerException("TaskConfig for taskName=" + taskName + " not found"));
	}
	
	@Override
	public String toString() {
		return "JobConfig [_id=" + _id + ", jobName=" + jobName + ", description=" + description + ", createdTimestamp="
				+ createdTimestamp + ", notifyEmail=" + notifyEmail + ", taskConfigs=" + taskConfigs + "]";
	}

}
