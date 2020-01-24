package gov.nyc.doitt.jobstatemanager.jobappconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class JobAppConfigMockerUpper {

	public String appName = "myApp";

	public List<JobAppConfig> createList(int listSize) throws Exception {

		List<JobAppConfig> jobs = new ArrayList<>();
		for (int i = 0; i < listSize; i++) {
			jobs.add(create(appName + i));
		}
		return jobs;
	}

	public JobAppConfig create() throws Exception {

		int i = new Random().nextInt(100) * -1;
		return create(appName + i);
	}

	public JobAppConfig create(String appName) throws Exception {

		JobAppConfig jobAppConfig = new JobAppConfig();

		jobAppConfig.setAppName(appName );
		jobAppConfig.setDescription("description" + appName);
		jobAppConfig.setNotifyEmail("josfriedman@doitt.nyc.gov");
		jobAppConfig.setMaxBatchSize(2);
		jobAppConfig.setMaxRetriesForError(3);

		return jobAppConfig;
	}
}
