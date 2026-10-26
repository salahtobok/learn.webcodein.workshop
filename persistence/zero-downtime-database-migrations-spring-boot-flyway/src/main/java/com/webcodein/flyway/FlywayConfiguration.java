package com.webcodein.flyway;

import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfiguration {

    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();
        return flyway;
    }

    @Bean
    public static BeanFactoryPostProcessor dependsOnFlywayPostProcessor() {
        return beanFactory -> {
            for (String name : beanFactory.getBeanNamesForType(EntityManagerFactory.class, true, false)) {
                beanFactory.getBeanDefinition(name).setDependsOn("flyway");
            }
        };
    }
}
