package org.danylo.service;

import org.danylo.model.User;
import org.danylo.repository.UserRepository;
import org.danylo.utils.UserBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @SpyBean
    @Qualifier("userRepository")
    UserRepository userRepositorySpy;

    @MockBean
    MailSender mailSenderMock;

    @Test
    void save() {
        User user = UserBuilder.buildNonExistedUser();
        userService.save(user);

        assertNotNull(user.getActivationCode());
        verify(userRepositorySpy).saveUser(user);
        verify(mailSenderMock).send(eq(user.getUsername()), eq("D-Fitness"), contains("Click here to activate your account"));
    }

    @Test
    void isUsernameExist_True_IfUserExist() {
        User user = new User();
        user.setUsername("gorin@gmail.com");

        assertTrue(userService.isUsernameExist(user));
    }

    @Test
    void isUsernameExist_False_IfUserNotExist() {
        User user = new User();
        user.setUsername("nouser@gmail.com");

        assertFalse(userService.isUsernameExist(user));
    }

    @Test
    @Sql(value = {"/insert-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/insert-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void activateByCode_UserIsActivated_IfActivationCodeExist() {
        userService.activateByCode("activate");

        verify(userRepositorySpy).updateUserStatus(any(User.class));
    }

    @Test
    @Sql(value = {"/insert-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/insert-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void activateByCode_UserIsNotActivated_IfActivationCodeNotExist() {
        assertThrows(UsernameNotFoundException.class, () -> userService.activateByCode("activate1"));
        verify(userRepositorySpy, never()).updateUserStatus(any(User.class));
    }
}
