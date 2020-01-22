package gov.nyc.doitt.jobstatemanager.task;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface TaskRepository extends MongoRepository<Task, String> {

}
