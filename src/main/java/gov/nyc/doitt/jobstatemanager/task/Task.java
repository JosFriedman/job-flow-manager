package gov.nyc.doitt.jobstatemanager.task;

import java.sql.Timestamp;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class Task {

	private String name;
	private String description;
	@Enumerated(EnumType.STRING)
	private Timestamp startTimestamp;
	private Timestamp endTimestamp;
	private TaskState state;
	private String errorReason;

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
				+ endTimestamp + ", state=" + state + ", errorReason=" + errorReason + "]";
	}

}
