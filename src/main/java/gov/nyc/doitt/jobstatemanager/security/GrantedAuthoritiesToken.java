package gov.nyc.doitt.jobstatemanager.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * 
 */
public class GrantedAuthoritiesToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;

	public GrantedAuthoritiesToken() {
		super(null);
		setAuthenticated(false);
	}

	public GrantedAuthoritiesToken(Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

}
