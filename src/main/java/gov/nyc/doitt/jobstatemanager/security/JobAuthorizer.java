package gov.nyc.doitt.jobstatemanager.security;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfig;
import gov.nyc.doitt.jobstatemanager.jobconfig.JobConfigService;

/**
 * Enforces authorization on HTTP requests
 */
public class JobAuthorizer {

	private static final Log log = LogFactory.getLog(JobAuthorizer.class);

	@Autowired
	private JobConfigService jobConfigService;

	@Autowired
	private Encryptor encryptor;

	@Autowired
	private JobNameExtractor jobNameExtractor;

	public boolean checkRequest(HttpServletRequest request) {

		try {
			if (log.isDebugEnabled()) {
				log.debug(String.format("checkRequest: request: %s", request));
			}

			if (SecurityContextHelper.isAdmin()) {
				return true;
			}

			String jobName = jobNameExtractor.getJobName(request);
			if (StringUtils.isEmpty(jobName)) {
				return false;
			}

			checkAuthorization(jobName);
			
			return true;
			
		} catch (Exception e) {
			log.error("Can't authorize request: " + request, e);
			return false;
		}
	}

	/**
	 * Check that jobName is accessible for caller; throw AccessDeniedException if not authorized
	 */
	private void checkAuthorization(String jobName) {

		JobConfig jobConfig = jobConfigService.getJobConfigDomain(jobName);

		if (!SecurityContextHelper.hasAuth()) {
			// auth required but no auth is context
			throw new AccessDeniedException("No authorization in context for request with jobName=" + jobName);
		}

		if (!encryptor.decrypt(jobConfig.getAuthToken()).equals(SecurityContextHelper.getToken())) {
			throw new AccessDeniedException("Not authorized for jobName=" + jobName);
		}
	}

}