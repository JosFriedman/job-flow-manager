package gov.nyc.doitt.jobstatusmanager;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import gov.nyc.doitt.jobstatusmanager.application.JobStatusManagerSpringBootApplication;

@SpringBootTest(classes = JobStatusManagerSpringBootApplication.class)
public class TestBase {

	@Autowired
	private ApplicationContext appContext;

	@Before
	public void init() throws Exception {
	}

}
