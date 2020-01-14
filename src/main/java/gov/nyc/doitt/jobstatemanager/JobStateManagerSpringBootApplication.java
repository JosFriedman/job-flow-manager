package gov.nyc.doitt.jobstatemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = { "gov.nyc.doitt.jobstatemanager" })
public class JobStateManagerSpringBootApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(JobStateManagerSpringBootApplication.class, args);
	}
}
