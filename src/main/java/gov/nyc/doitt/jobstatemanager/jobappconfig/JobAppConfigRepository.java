package gov.nyc.doitt.jobstatemanager.jobappconfig;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface JobAppConfigRepository extends MongoRepository<JobAppConfig, String> {

	List<JobAppConfig> findAllByOrderByAppNameAsc();

	JobAppConfig findByAppName(String appName);

	boolean existsByAppName(String appName);

	String deleteByAppName(String appName);

}
