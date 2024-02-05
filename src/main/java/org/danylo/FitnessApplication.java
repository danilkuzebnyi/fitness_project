package org.danylo;

import org.danylo.config.FlywayConfiguration;
import org.danylo.service.TestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class FitnessApplication {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext = SpringApplication.run(FitnessApplication.class, args);
        FlywayConfiguration databaseMigration = applicationContext
                .getBean("databaseMigration", FlywayConfiguration.class);
        databaseMigration.applyDatabaseMigrations();
        TestService testService = (TestService) applicationContext.getBean("testService");
        while (true) {
            testService.printRandomNumber();
            Thread.sleep(1000);
        }
    }
}