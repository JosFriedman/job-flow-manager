package gov.nyc.doitt.jobflowmanager.domain.jobflow;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobFlow;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobStatus;

@Repository
interface JobFlowRepository extends JpaRepository<JobFlow, Integer> {

//	@Lock(LockModeType.PESSIMISTIC_READ)
	List<JobFlow> findByStatusInAndErrorCountLessThan(List<JobStatus> statuses, int errorCount, Pageable pageable);

	@Lock(LockModeType.PESSIMISTIC_READ)
	List<JobFlow> findByAppIdAndStatusInAndErrorCountLessThan(String appId, List<JobStatus> statuses, int errorCount,
			Pageable pageable);

	boolean existsByAppIdAndJobId(String appId, String jobId);


	String deleteByAppIdAndJobId(String appId, String jobId);

	JobFlow findByAppIdAndJobId(String appId, String jobId);

	JobFlow getByAppIdAndJobId(String appId, String jobId);
}
