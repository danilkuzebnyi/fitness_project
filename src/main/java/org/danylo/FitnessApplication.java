package org.danylo;

import org.danylo.context.ExcelApplicationContext;
import org.danylo.context.ExcelContextTestClass;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class FitnessApplication {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ExcelApplicationContext("context.xlsx");
        var excelContextTestClass = (ExcelContextTestClass) applicationContext.getBean("excelContextTestClass");
        excelContextTestClass.sayHello();
        excelContextTestClass.callChildren();
//        SpringApplication.run(FitnessApplication.class, args);
//        FlywayConfiguration databaseMigration = applicationContext
//                .getBean("databaseMigration", FlywayConfiguration.class);
//        databaseMigration.applyDatabaseMigrations();
    }
}