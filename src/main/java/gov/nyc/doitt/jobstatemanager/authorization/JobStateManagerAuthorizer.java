package gov.nyc.doitt.jobstatemanager.authorization;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Enforces authorization on HTTP requests
 */
public class JobStateManagerAuthorizer {

	private static final Log log = LogFactory.getLog(JobStateManagerAuthorizer.class);

	@Autowired
	private JobNameExtractor jobNameExtractor;

	public boolean checkRequest(HttpServletRequest request) {

		try {
			if (log.isDebugEnabled()) {
				log.debug(String.format("checkRequest: request: %s", request));
			}

//			String siteId = jobNameExtractor.getSiteId(request);

			return true;
		} catch (Exception e) {
			log.error("Can't authorize request: " + request, e);
			return false;
		}
	}
}