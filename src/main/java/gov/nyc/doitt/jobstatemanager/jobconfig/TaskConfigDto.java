package gov.nyc.doitt.jobstatemanager.jobconfig;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TaskConfigDto {

	private String name;
	private String description;
	private int maxBatchSize;
	private int maxRetriesForError;

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
		return "TaskConfigDto [name=" + name + ", description=" + description + ", maxBatchSize=" + maxBatchSize
				+ ", maxRetriesForError=" + maxRetriesForError + "]";
	}

}
