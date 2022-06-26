package org.danylo.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserRepositoryTest {
    private static UserRepository userRepository;

    @BeforeEach
    void init() {
        userRepository = new UserRepository();
    }

    @Test
    void testFindByUsername() {
        System.out.println(userRepository.findByUsername("yakusheva@gmail.com").getUsername());
        Assertions.assertEquals("yakusheva@gmail.com", userRepository.findByUsername("yakusheva@gmail.com").getUsername());
    }
}