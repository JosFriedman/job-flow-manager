package gov.nyc.doitt.jobstatusmanager.domain.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Component;

@Component
public class JobStatusMockerUpper {

	public List<JobStatus> createList(int listSize) throws Exception {

		int id = new Random().nextInt(100) * -1;

		List<JobStatus> jobStatuss = new ArrayList<>();
		for (int i = 0; i < listSize; i++) {
			jobStatuss.add(create(id - i));
		}
		return jobStatuss;
	}

	public JobStatus create() throws Exception {

		int id = new Random().nextInt(100) * -1;
		return create(id);
	}

	private JobStatus create(int id) throws Exception {

		JobStatus jobStatus = new JobStatus();

		FieldUtils.writeField(jobStatus, "id", id, true);
	
		FieldUtils.writeField(jobStatus, "appId", "appId_ABC", true);
		FieldUtils.writeField(jobStatus, "jobId", "jobId" + id, true);
		FieldUtils.writeField(jobStatus, "description", "description" + id, true);

		// make very old so it is found first
		FieldUtils.writeField(jobStatus, "jobCreated", new Timestamp(System.currentTimeMillis() - 900000000000L), true);

		FieldUtils.writeField(jobStatus, "description", "description" + id, true);

		return jobStatus;
	}
}
