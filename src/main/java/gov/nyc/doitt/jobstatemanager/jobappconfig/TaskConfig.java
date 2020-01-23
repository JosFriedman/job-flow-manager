package gov.nyc.doitt.jobstatemanager.jobappconfig;

import java.sql.Timestamp;

public class TaskConfig {

	private String _id;

	private String name;
	private Integer sequence;
	private String description;
	private Timestamp createdTimestamp;

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

}
