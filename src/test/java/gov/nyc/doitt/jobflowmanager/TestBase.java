package gov.nyc.doitt.jobflowmanager;

import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = JobFlowManagerSpringBootApplication.class)
public class TestBase {

	@Before
	public void init() throws Exception {
	}

}
