package org.danylo;

import org.danylo.config.DatabaseMigrationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CourseProjectApplication {
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(CourseProjectApplication.class, args);
        DatabaseMigrationConfiguration databaseMigration = applicationContext
                .getBean("databaseMigration", DatabaseMigrationConfiguration.class);
        databaseMigration.applyDatabaseMigrations();
    }
}