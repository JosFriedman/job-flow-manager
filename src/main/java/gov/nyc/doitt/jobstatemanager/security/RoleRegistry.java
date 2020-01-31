package gov.nyc.doitt.jobstatemanager.security;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Registry of admin security roles
 */
@Component
class RoleRegistry {

	@Autowired
	private Encryptor encryptor;

//	@Value("${admin.tokens}")
	private String[] adminAuthTokens = { "wdxpirpCI0GUP913pciZ6RZrqdpSgH8LxR89ysXxmT0VKrnIu9A4oO3Hhxe0sWJao5PWtSqraPNu0CISh4vMS29VlNpu0KIL+DXa7D3Y6AQ=" };

	private Set<String> adminAuthTokenSet;

	boolean isAdmin(String s) {
		return getAdminAuthTokenSet().contains(s);
	}

	private synchronized Set<String> getAdminAuthTokenSet() {
		if (adminAuthTokenSet == null) {
			adminAuthTokenSet = Arrays.asList(adminAuthTokens).stream().map(p -> encryptor.decrypt(p)).collect(Collectors.toSet());
		}
		return adminAuthTokenSet;
	}
}
