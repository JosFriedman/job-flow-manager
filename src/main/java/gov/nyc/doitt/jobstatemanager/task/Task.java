package gov.nyc.doitt.jobstatemanager.task;

import java.sql.Timestamp;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class Task {

	private String name;
	private String description;
	private Timestamp startTimestamp;
	private Timestamp endTimestamp;
	@Enumerated(EnumType.STRING)
	private TaskState state;
	private String errorReason;
	private Boolean archived;

	public Task() {
	}

	public Task(String name) {
		this.name = name;
		startTimestamp = new Timestamp(System.currentTimeMillis());
		state = TaskState.PROCESSING;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public TaskState getState() {
		return state;
	}

	public void setState(TaskState state) {
		this.state = state;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}


	public Boolean getArchived() {
		return archived == null ? false : archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}

	public void endWithSuccess() {
		endTimestamp = new Timestamp(System.currentTimeMillis());
		this.state = TaskState.COMPLETED;
	}

	public void endWithError(String errorReason) {

		endTimestamp = new Timestamp(System.currentTimeMillis());
		state = TaskState.ERROR;
		this.errorReason = errorReason;
	}

	@Override
	public String toString() {
		return "Task [name=" + name + ", description=" + description + ", startTimestamp=" + startTimestamp + ", endTimestamp="
				+ endTimestamp + ", state=" + state + ", errorReason=" + errorReason + ", archived=" + archived + "]";
	}

}
