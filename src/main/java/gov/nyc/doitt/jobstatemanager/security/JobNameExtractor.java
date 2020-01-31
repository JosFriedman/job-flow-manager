package gov.nyc.doitt.jobstatemanager.security;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Extracts the JOB_NAME_PARAM value out of HttpServletRequest
 */
@Component
public class JobNameExtractor {

	private Logger logger = LoggerFactory.getLogger(JobNameExtractor.class);

	private static final String JOB_NAME_PARAM = "jobName";

	/**
	 * Get the JOB_NAME_PARAM value out of request
	 * 
	 * @param request
	 * @return jobName value if JOB_NAME_PARAM exists, else null
	 */
	public String getJobName(HttpServletRequest request) {

		logger.debug("Attempting to extract jobName for param {} from request: {}", JOB_NAME_PARAM, request.getRequestURI());
		// if no JOB_NAME_PARAM could be an ADMIN
		return request.getParameter(JOB_NAME_PARAM);
	}
}
