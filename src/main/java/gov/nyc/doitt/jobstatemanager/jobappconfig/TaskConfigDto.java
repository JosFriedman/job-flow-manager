package gov.nyc.doitt.jobstatemanager.jobappconfig;

import java.sql.Timestamp;

public class TaskConfigDto {

	private String name;
	private Integer sequence;
	private String description;
	private Timestamp createdTimestamp;

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

	@Override
	public String toString() {
		return "TaskConfigDto [name=" + name + ", sequence=" + sequence + ", description=" + description + ", createdTimestamp="
				+ createdTimestamp + "]";
	}

}
