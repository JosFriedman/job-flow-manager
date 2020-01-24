package gov.nyc.doitt.jobstatemanager.job;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import gov.nyc.doitt.jobstatemanager.common.JobStateManagerException;
import gov.nyc.doitt.jobstatemanager.task.Task;

@Document
public class Job {

	@Id
	@GenericGenerator(name = "db-uuid", strategy = "guid")
	@GeneratedValue(generator = "db-uuid")
	private String _id;

	private String appName;
	private String jobId;
	private String description;
	private Timestamp createdTimestamp;
	@Enumerated(EnumType.STRING)
	private JobState state;
	private String nextTaskName;

	private List<Task> tasks = new ArrayList<>();

	public Job() {
		createdTimestamp = new Timestamp(System.currentTimeMillis());
		state = JobState.READY;
	}

	public String get_id() {
		return _id;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
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

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public void startTask(Task task) {
		tasks.add(task);
		state = JobState.PROCESSING;
	}

	public String getNextTaskName() {
		return nextTaskName;
	}

	public void setNextTaskName(String nextTaskName) {
		this.nextTaskName = nextTaskName;
	}

	public int getTotalErrorCountForTask(String taskName) {
		return tasks.stream().filter(p -> p.getName().equals(taskName)).mapToInt(p -> p.getErrorCount()).sum();
	}

	public Task getLastTask(String taskName) {

		if (CollectionUtils.isEmpty(tasks)) {
			throw new JobStateManagerException("tasks for " + jobId + " is empty");
		}

		// get last task
		Task task = tasks.get(tasks.size() - 1);
		if (!task.getName().equals(taskName)) {
			throw new JobStateManagerException(
					String.format("Given task name '%s' != Task's name '%s': ", taskName, task.getName()));
		}
		return task;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appName == null) ? 0 : appName.hashCode());
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
		if (appName == null) {
			if (other.appName != null)
				return false;
		} else if (!appName.equals(other.appName))
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
		return "Job [_id=" + _id + ", appName=" + appName + ", jobId=" + jobId + ", description=" + description
				+ ", createdTimestamp=" + createdTimestamp + ", state=" + state + ", nextTaskName=" + nextTaskName + ", tasks="
				+ tasks + "]";
	}

}
