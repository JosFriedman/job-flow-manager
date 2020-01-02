package gov.nyc.doitt.jobstatemanager;

import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;

import gov.nyc.doitt.jobstatemanager.JobStateManagerSpringBootApplication;

@SpringBootTest(classes = JobStateManagerSpringBootApplication.class)
public class TestBase {

	@Before
	public void init() throws Exception {
	}

}
