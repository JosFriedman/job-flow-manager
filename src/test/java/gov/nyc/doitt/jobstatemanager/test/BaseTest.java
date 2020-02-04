package gov.nyc.doitt.jobstatemanager.test;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nyc.doitt.jobstatemanager.AppConfig;
import gov.nyc.doitt.jobstatemanager.JobStateManagerSpringBootApplication;
import gov.nyc.doitt.jobstatemanager.security.JobAuthorizationToken;
import gov.nyc.doitt.jobstatemanager.security.Role;

@Configuration
@PropertySource("classpath:application.properties")
@ContextConfiguration(classes = { AppConfig.class })
@WebAppConfiguration
@SpringBootTest(classes = JobStateManagerSpringBootApplication.class)
public abstract class BaseTest {

	@Value("${server.servlet.context-path}")
	private String contextRoot;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private ApplicationContext applicationContext;

	@BeforeClass
	public static void baseClassBeforeClass() {
		setAuthentication(null, Role.ROLE_USER.toString());
	}

	@SuppressWarnings("serial")
	protected static GrantedAuthority createGrantedAuthority(final String authority) {
		return new GrantedAuthority() {

			@Override
			public String getAuthority() {
				return authority;
			}
		};
	}

	protected static void setAuthentication(String token, String... roles) {
		SecurityContextHolder.getContext().setAuthentication(new JobAuthorizationToken(token,
				Arrays.asList(roles).stream().map(p -> createGrantedAuthority(p)).collect(Collectors.toList())));
	}

	protected String getContextRoot() {
		return contextRoot;
	}

	protected WebApplicationContext getWac() {
		return wac;
	}

	protected ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	protected String asJsonString(Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
