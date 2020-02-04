package gov.nyc.doitt.jobstatemanager.jobconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class JobConfigMockerUpper {

	public String jobName = "myJob";

	public static final String NON_ADMIN_AUTH_TOKEN = "d8OwgEKytnEk8e0CttazSTCrAnomX76wyuNl1Ow/ealh/iooQKGpINMIhnAK4vUFCTJCwG8M7ZKhivVbE87DFy7FS2JMewC9Y2CzG74AIK8=";

	public List<JobConfig> createList(int listSize) throws Exception {

		List<JobConfig> jobs = new ArrayList<>();
		for (int i = 0; i < listSize; i++) {
			jobs.add(create(jobName + i));
		}
		return jobs;
	}

	public JobConfig create() throws Exception {

		int i = new Random().nextInt(100) * -1;
		return create(jobName + i);
	}

	public JobConfig create(String jobName) throws Exception {

		JobConfig jobConfig = new JobConfig();

		jobConfig.setJobName(jobName);
		jobConfig.setDescription("description" + jobName);
		jobConfig.setNotifyEmail("josfriedman@doitt.nyc.gov");
		jobConfig.setAuthToken(NON_ADMIN_AUTH_TOKEN);

		ArrayList<TaskConfig> taskConfigs = new ArrayList<>();
		for (int j = 0; j < 3; j++) {

			TaskConfig taskConfig = new TaskConfig();
			taskConfig.setName("name" + j);
			taskConfig.setDescription("description" + j);
			taskConfig.setMaxBatchSize(j + 50);
			taskConfig.setMaxRetriesForError(j);
			taskConfigs.add(taskConfig);
		}

		jobConfig.setTaskConfigs(taskConfigs);
		return jobConfig;
	}
}
