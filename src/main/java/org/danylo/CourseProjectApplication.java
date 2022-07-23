package org.danylo;

import org.danylo.db.DatabaseMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CourseProjectApplication {
    private static final Logger logger = LoggerFactory.getLogger(CourseProjectApplication.class);

    public static void main(String[] args) {
        System.out.println("1 - WE ARE HERE applicationContext");
        logger.info("1 - LOGGER WE ARE HERE applicationContext");
        ApplicationContext applicationContext = SpringApplication.run(CourseProjectApplication.class, args);
        System.out.println("2 - WE ARE HERE applicationContext");
        logger.info("2 - LOGGER WE ARE HERE applicationContext");
        DatabaseMigration databaseMigration = applicationContext.getBean("databaseMigration", DatabaseMigration.class);
        databaseMigration.applyDatabaseMigrations();
    }
}