package gov.nyc.doitt.jobstatemanager.security;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Helper to get Authentication data from Spring SecurityContext
 */
public class SecurityContextHelper {

	private SecurityContextHelper() {
	}

	public static String getToken() {

		if (!hasAuth()) {
			throw new IllegalStateException("Calling getToken without first verifying that SecurityContext has a token");
		}
		return getJobAuthorizationToken().getToken();
	}

	public static JobAuthorizationToken getJobAuthorizationToken() {

		return ((JobAuthorizationToken) SecurityContextHolder.getContext().getAuthentication());
	}

	public static boolean hasAuth() {

		JobAuthorizationToken jobAuthorizationToken = getJobAuthorizationToken();

		return jobAuthorizationToken != null && jobAuthorizationToken.getToken() != null;
	}

	public static boolean isAdmin() {

		JobAuthorizationToken jobAuthorizationToken = getJobAuthorizationToken();
		return jobAuthorizationToken == null || jobAuthorizationToken.getToken() == null ? false
				: jobAuthorizationToken.getAuthorities().stream()
						.anyMatch(p -> p.getAuthority().equalsIgnoreCase(Role.ROLE_ADMIN.toString()));

	}

}
