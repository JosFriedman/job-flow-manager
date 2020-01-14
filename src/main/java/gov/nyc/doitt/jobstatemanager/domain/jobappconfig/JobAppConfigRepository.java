package gov.nyc.doitt.jobstatemanager.domain.jobappconfig;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface JobAppConfigRepository extends MongoRepository<JobAppConfig, String> {

	List<JobAppConfig> findAllByOrderByAppIdAsc();

	JobAppConfig findByAppId(String appId);

	boolean existsByAppId(String appId);

	String deleteByAppId(String appId);

}
