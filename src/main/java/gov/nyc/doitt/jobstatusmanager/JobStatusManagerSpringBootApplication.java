package gov.nyc.doitt.jobstatusmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobStatusManagerSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobStatusManagerSpringBootApplication.class, args);
	}
}
