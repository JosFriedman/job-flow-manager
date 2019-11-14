package gov.nyc.doitt.jobflowmanager;


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
        entityManagerFactoryRef = "jobFlowManagerEntityManagerFactory",
        transactionManagerRef = "jobFlowManagerTransactionManager",
        basePackages = {"gov.nyc.doitt.jobflowmanager"}
)
public class AppConfig {

    @Primary
    @Bean(name = "jobFlowManagerDataSource")
    @ConfigurationProperties(prefix = "jobflow.datasource")
    public DataSource jobFlowManagerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "jobFlowManagerEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("jobFlowManagerDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("gov.nyc.doitt.jobflowmanager")
                .persistenceUnit("josfriedman")
                .build();
    }

    @Primary
    @Bean(name = "jobFlowManagerTransactionManager")
    public PlatformTransactionManager jobFlowManagerTransactionManager(
            @Qualifier("jobFlowManagerEntityManagerFactory") EntityManagerFactory
                    jobFlowManagerEntityManagerFactory
    ) {
        return new JpaTransactionManager(jobFlowManagerEntityManagerFactory);
    }
}
