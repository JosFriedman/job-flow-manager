package gov.nyc.doitt.jobstatemanager.jobappconfig;

import java.sql.Timestamp;

public class TaskConfig {

	private String _id;

	private String name;
	private Integer sequence;
	private String description;
	private Timestamp createdTimestamp;
	private int maxBatchSize;
	private int maxRetriesForError;

	public TaskConfig() {
		createdTimestamp = new Timestamp(System.currentTimeMillis());
	}

	public String get_id() {
		return _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
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
	public String toString() {
		return "TaskConfig [_id=" + _id + ", name=" + name + ", sequence=" + sequence + ", description=" + description
				+ ", createdTimestamp=" + createdTimestamp + ", maxBatchSize=" + maxBatchSize + ", maxRetriesForError="
				+ maxRetriesForError + "]";
	}

}
