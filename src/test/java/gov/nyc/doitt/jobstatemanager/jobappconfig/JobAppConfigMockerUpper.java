package gov.nyc.doitt.jobstatemanager.jobappconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class JobAppConfigMockerUpper {

	public String appId = "myApp";

	public List<JobAppConfig> createList(int listSize) throws Exception {

		List<JobAppConfig> jobs = new ArrayList<>();
		for (int i = 0; i < listSize; i++) {
			jobs.add(create(appId + i));
		}
		return jobs;
	}

	public JobAppConfig create() throws Exception {

		int i = new Random().nextInt(100) * -1;
		return create(appId + i);
	}

	public JobAppConfig create(String appId) throws Exception {

		JobAppConfig jobAppConfig = new JobAppConfig();

		jobAppConfig.setAppId(appId );
		jobAppConfig.setDescription("description" + appId);
		jobAppConfig.setNotifyEmail("josfriedman@doitt.nyc.gov");
		jobAppConfig.setMaxBatchSize(2);
		jobAppConfig.setMaxRetriesForError(3);

		return jobAppConfig;
	}
}
