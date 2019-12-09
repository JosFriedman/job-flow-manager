package gov.nyc.doitt.jobflowmanager.domain.jobflow.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "JOB_FLOW")
public class JobFlow {

	@Id
	@GenericGenerator(name = "db-uuid", strategy = "guid")
	@GeneratedValue(generator = "db-uuid")
	@Column(name = "ID")
	private String id;

	@Column(name = "APP_ID")
	private String appId;

	@Column(name = "JOB_ID")
	private String jobId;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "JOB_CREATED_TIMESTAMP")
	private Timestamp jobCreatedTimestamp = new Timestamp(System.currentTimeMillis());

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private JobStatus status;

	@Column(name = "START_TIMESTAMP")
	private Timestamp startTimestamp;

	@Column(name = "END_TIMESTAMP")
	private Timestamp endTimestamp;

	@Column(name = "ERROR_COUNT")
	private int errorCount;

	@Version
	@Column(name = "MULTI_INSTANCE_CTRL")
	private int multiInstanceCtrl;

	public String getId() {
		return id;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
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

	public Timestamp getJobCreatedTimestamp() {
		return jobCreatedTimestamp;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	public void setStatusSmartly(JobStatus status) {
		this.status = status;
		endTimestamp = new Timestamp(System.currentTimeMillis());
		if (this.status == JobStatus.ERROR) {
			errorCount++;
		}
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

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public int incrementErrorCount() {
		return errorCount++;
	}

	public int getMultiInstanceCtrl() {
		return multiInstanceCtrl;
	}

	public void setMultiInstanceCtrl(int multiInstanceCtrl) {
		this.multiInstanceCtrl = multiInstanceCtrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((endTimestamp == null) ? 0 : endTimestamp.hashCode());
		result = prime * result + errorCount;
		result = prime * result + ((jobCreatedTimestamp == null) ? 0 : jobCreatedTimestamp.hashCode());
		result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
		result = prime * result + multiInstanceCtrl;
		result = prime * result + ((startTimestamp == null) ? 0 : startTimestamp.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		JobFlow other = (JobFlow) obj;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (endTimestamp == null) {
			if (other.endTimestamp != null)
				return false;
		} else if (!endTimestamp.equals(other.endTimestamp))
			return false;
		if (errorCount != other.errorCount)
			return false;
		if (jobCreatedTimestamp == null) {
			if (other.jobCreatedTimestamp != null)
				return false;
		} else if (!jobCreatedTimestamp.equals(other.jobCreatedTimestamp))
			return false;
		if (jobId == null) {
			if (other.jobId != null)
				return false;
		} else if (!jobId.equals(other.jobId))
			return false;
		if (multiInstanceCtrl != other.multiInstanceCtrl)
			return false;
		if (startTimestamp == null) {
			if (other.startTimestamp != null)
				return false;
		} else if (!startTimestamp.equals(other.startTimestamp))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JobFlow [id=" + id + ", appId=" + appId + ", jobId=" + jobId + ", description=" + description
				+ ", jobCreatedTimestamp=" + jobCreatedTimestamp + ", status=" + status + ", startTimestamp=" + startTimestamp
				+ ", endTimestamp=" + endTimestamp + ", errorCount=" + errorCount + ", multiInstanceCtrl=" + multiInstanceCtrl
				+ "]";
	}

}
