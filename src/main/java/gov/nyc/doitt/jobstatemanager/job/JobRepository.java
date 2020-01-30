package gov.nyc.doitt.jobstatemanager.job;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {

	List<Job> findByJobNameAndStateInAndNextTaskName(String jobName, List<JobState> states, String taskName, Pageable pageable);

	List<Job> findByJobNameAndJobIdInAndStateInAndNextTaskName(String jobName, List<String> jobIds, List<JobState> states,
			String taskName);

	boolean existsByJobNameAndJobId(String jobName, String jobId);

	String deleteByJobNameAndJobId(String jobName, String jobId);

	Job findByJobNameAndJobId(String jobName, String jobId);

	List<Job> findByJobName(String jobName, Sort sort);

	List<Job> findByJobNameAndState(String jobName, String state, Sort sort);

	List<Job> findByJobNameAndJobIdIn(String jobName, List<String> jobIds);

	List<Job> findAll(Sort sort);

}
