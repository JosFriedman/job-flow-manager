package gov.nyc.doitt.jobstatemanager.security;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Extracts authentication params from Http request and returns a PreAuthenticatedAuthenticationToken
 */
@Component
public class AuthParamsExtractor implements TokenExtractor {

	private static final Log log = LogFactory.getLog(AuthParamsExtractor.class);

	public static final String ACCESS_TOKEN = "Authorization";

	@Autowired
	private JobNameExtractor jobNameExtractor;

	@Override
	public Authentication extract(HttpServletRequest request) {

		if (log.isDebugEnabled()) {
			log.debug(String.format("extract: request: %s", request));
		}
		ValidateInParams validateInParams = null;
		String accessToken = extractHeaderToken(request);

		if (!StringUtils.isEmpty(accessToken)) {

			try {
				String jobName = jobNameExtractor.getJobName(request); // may be null
				validateInParams = new ValidateInParams(accessToken, jobName);
			} catch (Exception e) {
				throw new UserValidationException(
						String.format("Error trying to get jobName from request: %s", request.getServletPath(), e));
			}

		} // ... else no validation to be done, i.e., validationInParams are null

		if (log.isDebugEnabled())

		{
			log.debug(String.format("extract: validateInParams: %s", validateInParams));
		}
		return new PreAuthenticatedAuthenticationToken("", validateInParams);

	}

	/**
	 * (Taken from BearerTokenExtractor)
	 * 
	 * Extract the OAuth bearer token from a header.
	 * 
	 * @param request The request.
	 * @return The token, or null if no OAuth authorization header was supplied.
	 */
	protected String extractHeaderToken(HttpServletRequest request) {
		Enumeration<String> headers = request.getHeaders(ACCESS_TOKEN);
		while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
			String value = headers.nextElement();
			if ((value.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase()))) {
				String authHeaderValue = value.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
				// Add this here for the auth details later. Would be better to change the signature of this method.
				request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE,
						value.substring(0, OAuth2AccessToken.BEARER_TYPE.length()).trim());
				int commaIndex = authHeaderValue.indexOf(',');
				if (commaIndex > 0) {
					authHeaderValue = authHeaderValue.substring(0, commaIndex);
				}
				return authHeaderValue;
			}
		}

		return null;
	}

}
