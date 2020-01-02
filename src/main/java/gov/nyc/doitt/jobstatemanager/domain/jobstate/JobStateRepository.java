package gov.nyc.doitt.jobstatemanager.domain.jobstate;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gov.nyc.doitt.jobstatemanager.domain.jobstate.model.JobState;
import gov.nyc.doitt.jobstatemanager.domain.jobstate.model.JobStatus;

@Repository
interface JobStateRepository extends MongoRepository<JobState, String> {

	@Lock(LockModeType.PESSIMISTIC_READ)
	List<JobState> findByAppIdAndStatusInAndErrorCountLessThan(String appId, List<JobStatus> statuses, int errorCount,
			Pageable pageable);

	boolean existsByAppIdAndJobId(String appId, String jobId);

	String deleteByAppIdAndJobId(String appId, String jobId);

	JobState findByAppIdAndJobId(String appId, String jobId);

	JobState getByAppIdAndJobId(String appId, String jobId);

	List<JobState> findByAppId(String appId);

	List<JobState> getByAppId(String appId);

	List<JobState> getByAppIdAndJobIdIn(String appId, List<String> jobIds);
}
