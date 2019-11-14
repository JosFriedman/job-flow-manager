package gov.nyc.doitt.jobflowmanager.domain.jobappconfig.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "JOB_APP_CONFIG")
public class JobAppConfig {

	@Id
	@Column(name = "ID")
	private long id;

	@Column(name = "APP_ID")
	private String appId;

}
