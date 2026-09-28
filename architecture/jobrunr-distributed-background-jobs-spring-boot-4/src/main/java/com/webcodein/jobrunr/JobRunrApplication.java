package com.webcodein.jobrunr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = { org.jobrunr.spring.autoconfigure.JobRunrAutoConfiguration.class })
public class JobRunrApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobRunrApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.jobrunr.scheduling.JobScheduler jobScheduler(javax.sql.DataSource dataSource, org.springframework.context.ApplicationContext applicationContext) {
        return org.jobrunr.configuration.JobRunr.configure()
                .useJobActivator(applicationContext::getBean)
                .useStorageProvider(org.jobrunr.storage.sql.common.SqlStorageProviderFactory
                          .using(dataSource))
                .useBackgroundJobServer()
                .useDashboard()
                .initialize()
                .getJobScheduler();
    }
}
