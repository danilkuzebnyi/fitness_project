package org.danylo;

import org.danylo.config.ApplicationConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CourseProjectApplicationTests {
    @Autowired
    ApplicationConfiguration appConfig;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(appConfig);
    }
}
