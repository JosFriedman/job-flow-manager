package gov.nyc.doitt.jobstatemanager.jobconfig;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface JobConfigRepository extends MongoRepository<JobConfig, String> {

	List<JobConfig> findAllByOrderByJobNameAsc();

	JobConfig findByJobName(String jobName);

	boolean existsByJobName(String jobName);

	String deleteByJobName(String jobName);

}
