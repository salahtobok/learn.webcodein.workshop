package com.webcodein.jobrunr;

import jakarta.annotation.PreDestroy;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication(exclude = { org.jobrunr.spring.autoconfigure.JobRunrAutoConfiguration.class })
public class JobRunrApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobRunrApplication.class, args);
    }

    @Bean
    public JobScheduler jobScheduler(DataSource dataSource, ApplicationContext applicationContext) {
        return JobRunr.configure()
                .useJobActivator(applicationContext::getBean)
                .useStorageProvider(SqlStorageProviderFactory.using(dataSource))
                .useBackgroundJobServer()
                .useDashboard()
                .initialize()
                .getJobScheduler();
    }

    @PreDestroy
    public void onDestroy() {
        JobRunr.destroy();
    }
}
