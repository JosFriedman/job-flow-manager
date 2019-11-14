package gov.nyc.doitt.jobflowmanager.domain.jobflow.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Component;

@Component
public class JobFlowMockerUpper {

	public List<JobFlow> createList(int listSize) throws Exception {

		int id = new Random().nextInt(100) * -1;

		List<JobFlow> jobFlows = new ArrayList<>();
		for (int i = 0; i < listSize; i++) {
			jobFlows.add(create(i));
		}
		return jobFlows;
	}

	public JobFlow create() throws Exception {

		int i = new Random().nextInt(100) * -1;
		return create(i);
	}

	private JobFlow create(int idx) throws Exception {

		JobFlow jobFlow = new JobFlow();

		FieldUtils.writeField(jobFlow, "appId", "appId_ABC", true);
		FieldUtils.writeField(jobFlow, "jobId", "jobId" + idx, true);
		FieldUtils.writeField(jobFlow, "description", "description" + idx, true);

		// make very old so it is found first
		FieldUtils.writeField(jobFlow, "jobCreatedTimestamp", new Timestamp(System.currentTimeMillis() - 900000000000L), true);

		FieldUtils.writeField(jobFlow, "description", "description" + idx, true);

		return jobFlow;
	}
}
