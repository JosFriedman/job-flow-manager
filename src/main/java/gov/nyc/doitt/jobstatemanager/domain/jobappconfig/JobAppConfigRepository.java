package gov.nyc.doitt.jobstatemanager.domain.jobappconfig;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gov.nyc.doitt.jobstatemanager.domain.jobappconfig.model.JobAppConfig;

@Repository
interface JobAppConfigRepository extends MongoRepository<JobAppConfig, String> {

	JobAppConfig findByAppId(String appId);

	boolean existsByAppId(String appId);

	String deleteByAppId(String appId);

}
