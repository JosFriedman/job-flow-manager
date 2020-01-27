package gov.nyc.doitt.jobstatemanager.job;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Component;

@Component
public class JobDtoMockerUpper {

	public String appName = "myApp";

	public List<JobDto> createList(int listSize) throws Exception {

		List<JobDto> jobs = new ArrayList<>();
		for (int i = 0; i < listSize; i++) {
			jobs.add(create(i));
		}
		return jobs;
	}

	public JobDto create() throws Exception {

		int i = new Random().nextInt(100) * -1;
		return create(i);
	}

	public JobDto create(int idx) throws Exception {

		JobDto jobDto = new JobDto();

		FieldUtils.writeField(jobDto, "appName", appName, true);
		FieldUtils.writeField(jobDto, "jobId", "jobId" + idx, true);
		FieldUtils.writeField(jobDto, "description", "description" + idx, true);

		// make very old so it is found first
		FieldUtils.writeField(jobDto, "createdTimestamp", new Timestamp(System.currentTimeMillis() - 9000000000000L), true);

		FieldUtils.writeField(jobDto, "description", "description" + idx, true);

		return jobDto;
	}
}
