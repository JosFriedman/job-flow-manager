package gov.nyc.doitt.jobstatemanager.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Custom AuthenticationManager that gets Content-API-specific authentication params and delegates to UserValidator to validate them
 */
@Component
public class UserAuthenticationManager implements AuthenticationManager {

	private static final Log log = LogFactory.getLog(UserAuthenticationManager.class);

	@Autowired
	private RoleRegistry roleRegistry;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		ValidateInParams validateInParams = (ValidateInParams) authentication.getCredentials();

		if (log.isDebugEnabled()) {
			log.debug("authenticate: " + validateInParams);
		}
		UserAccount userAccount = null;
		if (validateInParams != null) {
//			// have enough to try to validate
//			try {
//				userAccount = userValidator.validate(validateInParams);
//			} catch (UserValidationException e) {
//				throw e;
//			} catch (Exception e) {
//				throw new UserValidationException(String.format("Can't validate user: clientId=%s, siteId=%s",
//						validateInParams.getContentApiClientId(), validateInParams.getSiteId()), e);
//			}

		} // ... else userAccount is null which is fine for endpoints or data not requiring authorization

		UserAuthenticationToken userAuthenticationToken = new UserAuthenticationToken(userAccount,
				createGrantedAuthorities(userAccount));
		userAuthenticationToken.setDetails(authentication.getDetails());
		userAuthenticationToken.setAuthenticated(true);
		return userAuthenticationToken;
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

	private List<GrantedAuthority> createGrantedAuthorities(UserAccount userAccount) {

		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.add(createGrantedAuthority("ROLE_ADMIN"));		
//		if (userAccount != null && roleRegistry.isAdmin(userAccount.getEmail())) {
//			grantedAuthorities.add(createGrantedAuthority(Role.ROLE_ADMIN.toString()));
//		}
		return grantedAuthorities;
	}

}