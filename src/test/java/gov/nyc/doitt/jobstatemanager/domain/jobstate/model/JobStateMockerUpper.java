package gov.nyc.doitt.jobstatemanager.domain.jobstate.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Component;

import gov.nyc.doitt.jobstatemanager.domain.jobstate.model.JobState;

@Component
public class JobStateMockerUpper {

	public String appId = "myApp1";

	public List<JobState> createList(int listSize) throws Exception {

		List<JobState> jobStates = new ArrayList<>();
		for (int i = 0; i < listSize; i++) {
			jobStates.add(create(i));
		}
		return jobStates;
	}

	public JobState create() throws Exception {

		int i = new Random().nextInt(100) * -1;
		return create(i);
	}

	private JobState create(int idx) throws Exception {

		JobState jobState = new JobState();

		FieldUtils.writeField(jobState, "appId", appId, true);
		FieldUtils.writeField(jobState, "jobId", "jobId" + idx, true);
		FieldUtils.writeField(jobState, "description", "description" + idx, true);

		// make very old so it is found first
		FieldUtils.writeField(jobState, "jobCreatedTimestamp", new Timestamp(System.currentTimeMillis() - 9000000000000L), true);

		FieldUtils.writeField(jobState, "description", "description" + idx, true);

		return jobState;
	}
}
