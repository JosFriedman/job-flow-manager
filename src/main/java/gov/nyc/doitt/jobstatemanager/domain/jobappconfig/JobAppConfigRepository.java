package gov.nyc.doitt.jobstatemanager.domain.jobappconfig;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gov.nyc.doitt.jobstatemanager.domain.job.model.Job;
import gov.nyc.doitt.jobstatemanager.domain.jobappconfig.model.JobAppConfig;

@Repository
interface JobAppConfigRepository extends MongoRepository<JobAppConfig, String> {

	List<JobAppConfig> findAllByOrderByAppIdAsc();

	JobAppConfig findByAppId(String appId);

	boolean existsByAppId(String appId);

	String deleteByAppId(String appId);

}
