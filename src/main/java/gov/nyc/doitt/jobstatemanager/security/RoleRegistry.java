package gov.nyc.doitt.jobstatemanager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Registry of security roles
 */
@Component
class RoleRegistry {

	@Autowired
	private Encryptor encryptor;

	@Value("${admin.auth.token}")
	private String adminAuthToken;

	private String decryptedAdminAuthToken;

	public Role getRole(String s) {
		return getDecryptedAdminAuthToken().equals(s) ? Role.ROLE_ADMIN : Role.ROLE_USER;
	}

	private synchronized String getDecryptedAdminAuthToken() {
		if (decryptedAdminAuthToken == null) {
			decryptedAdminAuthToken = encryptor.decrypt(adminAuthToken);
		}
		return decryptedAdminAuthToken;
	}
}
