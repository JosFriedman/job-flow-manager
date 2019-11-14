package gov.nyc.doitt.jobflowmanager.domain.jobappconfig;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobFlow;
import gov.nyc.doitt.jobflowmanager.domain.jobflow.model.JobStatus;

@Repository
interface JobAppConfigRepository extends JpaRepository<JobFlow, Integer> {

	@Lock(LockModeType.PESSIMISTIC_READ)
	List<JobFlow> findByStatusInAndErrorCountLessThan(List<JobStatus> statuses, int errorCount, Pageable pageable);

}
