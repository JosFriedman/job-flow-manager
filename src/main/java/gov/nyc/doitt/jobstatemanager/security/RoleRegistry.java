package gov.nyc.doitt.jobstatemanager.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Registry of Content API security roles
 */
@Component
class RoleRegistry implements InitializingBean {

//	@Value("${admin.users}")
	private String adminUsers = "josfriedman@doitt.nyc.gov";

	private Set<String> adminUserSet;

	boolean isAdmin(String userEmail) {
		return userEmail == null ? false : adminUserSet.contains(userEmail);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		String[] users = adminUsers.split("\\s*,\\s*");
		adminUserSet = new HashSet<String>(Arrays.asList(users));
	}

	public Set<String> getAdminUserSet() {
		return adminUserSet;
	}
}
