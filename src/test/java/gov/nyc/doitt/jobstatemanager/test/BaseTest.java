package gov.nyc.doitt.jobstatemanager.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nyc.doitt.jobstatemanager.AppConfig;
import gov.nyc.doitt.jobstatemanager.JobStateManagerSpringBootApplication;

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
