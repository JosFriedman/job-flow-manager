package gov.nyc.doitt.jobflowmanager;

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
@EnableJpaRepositories(
//        entityManagerFactoryRef = "jobFlowManagerEntityManagerFactory",
//        transactionManagerRef = "jobFlowManagerTransactionManager",
		basePackages = { "gov.nyc.doitt.jobflowmanager" })
public class AppConfig {

//    @Primary
//    @Bean(name = "jobFlowManagerDataSource")
//    @ConfigurationProperties(prefix = "jobflow.datasource")
//    public DataSource jobFlowManagerDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Primary
//    @Bean(name = "jobFlowManagerEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean
//    entityManagerFactory(
//            EntityManagerFactoryBuilder builder,
//            @Qualifier("jobFlowManagerDataSource") DataSource dataSource
//    ) {
//        return builder
//                .dataSource(dataSource)
//                .packages("gov.nyc.doitt.jobflowmanager")
//                .persistenceUnit("josfriedman")
//                .build();
//    }

//    @Primary
//    @Bean(name = "jobFlowManagerTransactionManager")
//    public PlatformTransactionManager jobFlowManagerTransactionManager(
//            @Qualifier("jobFlowManagerEntityManagerFactory") EntityManagerFactory
//                    jobFlowManagerEntityManagerFactory
//    ) {
//        return new JpaTransactionManager(jobFlowManagerEntityManagerFactory);
//    }

	@Autowired
	MongoDbFactory mongoDbFactory;

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, getDefaultMongoConverter());
		return mongoTemplate;
	}

	@Bean
	public MappingMongoConverter getDefaultMongoConverter() throws Exception {
		MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory),
				new MongoMappingContext());
		
		converter.setCustomConversions(new CustomConversions(Collections.singletonList(DateToTimestampConverter.INSTANCE)));
		return converter;
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
	 
	  public java.sql.Timestamp  convert(Date source) {
	      return source == null ? null : new java.sql.Timestamp(source.getTime());
	  }
	}
