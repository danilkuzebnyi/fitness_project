package org.danylo;

import org.danylo.config.FlywayConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class FitnessApplication {
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(FitnessApplication.class, args);
        FlywayConfiguration databaseMigration = applicationContext
                .getBean("databaseMigration", FlywayConfiguration.class);
        databaseMigration.applyDatabaseMigrations();
    }
}