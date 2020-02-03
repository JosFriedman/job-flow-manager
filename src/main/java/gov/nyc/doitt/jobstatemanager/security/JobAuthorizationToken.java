package gov.nyc.doitt.jobstatemanager.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * 
 */
public class JobAuthorizationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;

	private String token;
	
	public JobAuthorizationToken(String token, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.token = token;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

	public String getToken() {
		return token;
	}
	
	

}
