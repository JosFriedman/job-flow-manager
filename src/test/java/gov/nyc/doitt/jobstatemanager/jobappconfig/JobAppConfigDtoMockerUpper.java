package gov.nyc.doitt.jobstatemanager.jobappconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class JobAppConfigDtoMockerUpper {

	public String appName = "myApp";

	public List<JobAppConfigDto> createList(int listSize) throws Exception {

		List<JobAppConfigDto> jobs = new ArrayList<>();
		for (int i = 0; i < listSize; i++) {
			jobs.add(create(i));
		}
		return jobs;
	}

	public JobAppConfigDto create() throws Exception {

		int i = new Random().nextInt(100) * -1;
		return create(i);
	}

	public JobAppConfigDto create(int idx) throws Exception {

		JobAppConfigDto jobAppConfigDto = new JobAppConfigDto();

		jobAppConfigDto.setAppName(appName + idx);
		jobAppConfigDto.setDescription("description" + idx);
		jobAppConfigDto.setNotifyEmail("josfriedman@doitt.nyc.gov");
		jobAppConfigDto.setMaxBatchSize(2);
		jobAppConfigDto.setMaxRetriesForError(3);

		return jobAppConfigDto;
	}
}
