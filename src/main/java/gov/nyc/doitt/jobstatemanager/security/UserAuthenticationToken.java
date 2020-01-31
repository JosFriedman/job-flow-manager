package gov.nyc.doitt.jobstatemanager.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * Holds UserAccount details
 * 
 */
public class UserAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;

	private final UserAccount userAccount;

	public UserAuthenticationToken(UserAccount userAccount) {
		super(null);
		this.userAccount = userAccount;
		setAuthenticated(false);
	}

	public UserAuthenticationToken(UserAccount userAccount, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.userAccount = userAccount;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return userAccount;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

}
