package gov.nyc.doitt.jobstatemanager.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import gov.nyc.doitt.jobstatemanager.common.ValidationException;

/**
 * Custom AuthenticationManager that gets authentication params and validates them
 */
@Component
public class JobStateManagerAuthenticationManager implements AuthenticationManager {

	private Logger logger = LoggerFactory.getLogger(JobStateManagerAuthenticationManager.class);

	@Autowired
	private RoleRegistry roleRegistry;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		ValidateInParams validateInParams = (ValidateInParams) authentication.getCredentials();

		logger.debug("authenticate: validateInParams: {}", validateInParams);

		if (validateInParams == null) {
			throw new ValidationException("authenticate: validateInParams is null");
		}

		boolean isAdmin = roleRegistry.isAdmin(validateInParams.getAccessToken());
		if (!isAdmin) {
		}
		GrantedAuthoritiesToken grantedAuthoritiesToken = new GrantedAuthoritiesToken(createGrantedAuthorities(isAdmin));
		grantedAuthoritiesToken.setDetails(authentication.getDetails());
		grantedAuthoritiesToken.setAuthenticated(true);
		return grantedAuthoritiesToken;
	}

	@SuppressWarnings("serial")
	private GrantedAuthority createGrantedAuthority(final String authority) {
		return new GrantedAuthority() {

			@Override
			public String getAuthority() {
				return authority;
			}
		};
	}

	private List<GrantedAuthority> createGrantedAuthorities(boolean admin) {

		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		if (admin) {
			grantedAuthorities.add(createGrantedAuthority("ROLE_ADMIN"));
		}
		return grantedAuthorities;
	}

}