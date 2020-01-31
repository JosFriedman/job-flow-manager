package gov.nyc.doitt.jobstatemanager.authorization;

/**
 * Params for user validation and for authorization
 */
public class ValidateInParams {

	private String accessToken;
	private String jobName;

	public ValidateInParams(String accessToken, String jobName) {
		this.accessToken = accessToken;
		this.jobName = jobName;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getJobName() {
		return jobName;
	}

	@Override
	public String toString() {
		return "ValidateInParams [accessToken=" + accessToken + ", jobName=" + jobName + "]";
	}

}
