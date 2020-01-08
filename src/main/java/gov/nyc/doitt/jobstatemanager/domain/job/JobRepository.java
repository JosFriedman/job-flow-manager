package gov.nyc.doitt.jobstatemanager.domain.job;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gov.nyc.doitt.jobstatemanager.domain.job.model.Job;
import gov.nyc.doitt.jobstatemanager.domain.job.model.JobState;

@Repository
interface JobRepository extends MongoRepository<Job, String> {

	List<Job> findByAppIdAndStateInAndErrorCountLessThan(String appId, List<JobState> states, int errorCount, Pageable pageable);

	boolean existsByAppIdAndJobId(String appId, String jobId);

	String deleteByAppIdAndJobId(String appId, String jobId);

	Job findByAppIdAndJobId(String appId, String jobId);

	Job getByAppIdAndJobId(String appId, String jobId);

	List<Job> findByAppId(String appId);

	List<Job> getByAppId(String appId);

	List<Job> getByAppIdAndJobIdIn(String appId, List<String> jobIds);
}
