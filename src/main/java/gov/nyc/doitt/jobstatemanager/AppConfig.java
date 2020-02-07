package gov.nyc.doitt.jobstatemanager;

import java.util.Collections;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.SessionSynchronization;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "gov.nyc.doitt.jobstatemanager" })
public class AppConfig {

	@Autowired
	private MongoDbFactory mongoDbFactory;

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, getDefaultMongoConverter());
		
		mongoTemplate.setSessionSynchronization(SessionSynchronization.ALWAYS); 
		
		return mongoTemplate;
	}

	@Bean
	public MappingMongoConverter getDefaultMongoConverter() throws Exception {
		MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory),
				new MongoMappingContext());

		converter.setCustomConversions(new CustomConversions(Collections.singletonList(DateToTimestampConverter.INSTANCE)));
		return converter;
	}
	
    @Bean
    public MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}

enum TimestampToDateConverter implements Converter<java.sql.Timestamp, Date> {
	INSTANCE;

	public Date convert(java.sql.Timestamp source) {
		return source == null ? null : new Date(source.getTime());
	}
}

enum DateToTimestampConverter implements Converter<Date, java.sql.Timestamp> {
	INSTANCE;

	public java.sql.Timestamp convert(Date source) {
		return source == null ? null : new java.sql.Timestamp(source.getTime());
	}
}
