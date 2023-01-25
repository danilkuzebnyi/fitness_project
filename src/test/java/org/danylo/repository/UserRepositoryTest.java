package org.danylo.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername() {
        Assertions.assertEquals("yakusheva@gmail.com", userRepository.findByUsername("yakusheva@gmail.com").getUsername());
    }
}