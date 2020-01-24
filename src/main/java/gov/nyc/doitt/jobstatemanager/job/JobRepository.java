package gov.nyc.doitt.jobstatemanager.job;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {

	List<Job> findByAppNameAndStateInAndNextTaskName(String appName, List<JobState> states, String taskName, Pageable pageable);

	List<Job> findByAppNameAndJobIdInAndStateInAndNextTaskName(String appName, List<String> jobIds, List<JobState> states,
			String taskName);

	boolean existsByAppNameAndJobId(String appName, String jobId);

	String deleteByAppNameAndJobId(String appName, String jobId);

	Job findByAppNameAndJobId(String appName, String jobId);

	List<Job> findByAppName(String appName, Sort sort);

	List<Job> findByAppNameAndState(String appName, String state, Sort sort);

	List<Job> findByAppNameAndJobIdIn(String appName, List<String> jobIds);

	List<Job> findAll(Sort sort);

}
