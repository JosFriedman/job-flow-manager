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

/**
 * Custom AuthenticationManager that gets authentication params and validates them
 */
@Component
public class JobAuthenticationManager implements AuthenticationManager {

	private Logger logger = LoggerFactory.getLogger(JobAuthenticationManager.class);

	@Autowired
	private RoleRegistry roleRegistry;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String authToken = (String) authentication.getCredentials();

		logger.debug("authenticate: authToken: {}", authToken);

		Role role = roleRegistry.getRole(authToken);
		JobAuthorizationToken jobAuthorizationToken = new JobAuthorizationToken(authToken, createGrantedAuthorities(role));
		jobAuthorizationToken.setDetails(authentication.getDetails());
		return jobAuthorizationToken;
	}

	private List<GrantedAuthority> createGrantedAuthorities(Role role) {

		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
			grantedAuthorities.add(createGrantedAuthority(role.toString()));
		return grantedAuthorities;
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


}