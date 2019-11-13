package gov.nyc.doitt.jobstatusmanager;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

public class TestBase {

	@Autowired
	private ApplicationContext appContext;

	@Before
	public void init() throws Exception {
	}

}
