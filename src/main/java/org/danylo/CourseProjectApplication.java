package org.danylo;

import org.danylo.db.DatabaseMigration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CourseProjectApplication {
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(CourseProjectApplication.class, args);
        DatabaseMigration databaseMigration = applicationContext.getBean("databaseMigration", DatabaseMigration.class);
        databaseMigration.applyDatabaseMigrations();
    }
}