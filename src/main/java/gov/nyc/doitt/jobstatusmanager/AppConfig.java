package gov.nyc.doitt.jobstatusmanager;


import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "jobStatusManagerEntityManagerFactory",
        transactionManagerRef = "jobStatusManagerTransactionManager",
        basePackages = {"gov.nyc.doitt.jobstatusmanager"}
)
public class AppConfig {

    @Primary
    @Bean(name = "jobStatusManagerDataSource")
    @ConfigurationProperties(prefix = "jobstatus.datasource")
    public DataSource jobStatusManagerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "jobStatusManagerEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("jobStatusManagerDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("gov.nyc.doitt.jobstatusmanager")
                .persistenceUnit("josfriedman")
                .build();
    }

    @Primary
    @Bean(name = "jobStatusManagerTransactionManager")
    public PlatformTransactionManager jobStatusManagerTransactionManager(
            @Qualifier("jobStatusManagerEntityManagerFactory") EntityManagerFactory
                    jobStatusManagerEntityManagerFactory
    ) {
        return new JpaTransactionManager(jobStatusManagerEntityManagerFactory);
    }
}
