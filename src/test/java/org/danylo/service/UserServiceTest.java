package org.danylo.service;

import org.danylo.model.User;
import org.danylo.repository.UserRepository;
import org.danylo.utils.UserBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    UserService userService;

    @SpyBean
    @Qualifier("userRepository")
    UserRepository userRepositorySpy;

    @MockBean
    MailSender mailSenderMock;

    @Test
    @Sql(value = {"/sql/restart-users-id-seq.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void save() {
        User user = UserBuilder.buildNonExistedUser();
        RequestContextHolder.currentRequestAttributes()
                .setAttribute("selectedCountry", user.getCountry(), RequestAttributes.SCOPE_SESSION);

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
    @Sql(value = {"/sql/restart-users-id-seq.sql", "/sql/insert-user.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void activateByCode_UserIsActivated_IfActivationCodeExist() {
        userService.activateByCode("activate");
        verify(userRepositorySpy).updateUserStatus(any(User.class));
    }

    @Test
    @Sql(value = {"/sql/restart-users-id-seq.sql", "/sql/insert-user.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void activateByCode_UserIsNotActivated_IfActivationCodeNotExist() {
        assertThrows(UsernameNotFoundException.class, () -> userService.activateByCode("activate1"));
        verify(userRepositorySpy, never()).updateUserStatus(any(User.class));
    }
}
