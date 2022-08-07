package org.danylo.service;

import io.sentry.Sentry;
import io.sentry.spring.tracing.SentrySpan;
import io.sentry.spring.tracing.SentryTransaction;
import org.danylo.logging.Log;
import org.danylo.model.User;
import org.danylo.repository.UserRepository;
import org.danylo.security.UserSecurity;
import org.danylo.sentry.SentryInstrumentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
@SentryTransaction(operation = "task")
@SentrySpan
public class UserService implements UserDetailsService {
    UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return UserSecurity.fromUser(user);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username);
    }

    public String save(User user, BindingResult bindingResult) {
        boolean isPasswordExisted = (boolean) SentryInstrumentation.createTransactionBoundToTheCurrentScope
                (() -> isPasswordExist(user), UserService.class, "isPasswordExist");
        boolean isUsernameExisted = (boolean) SentryInstrumentation.createTransactionBoundToTheCurrentScope
                (() -> isUsernameExist(user), UserService.class, "isUsernameExist");
        String returnedPage = "redirect:/login";
        String userExistMessage = "User " + user.getUsername() + " exists";
        if (isUsernameExisted) {
            bindingResult.rejectValue("username", "user.username","An account already exists for this email");
            Log.logger.info(userExistMessage);
            Sentry.captureMessage(userExistMessage);
        }
        if (isPasswordExisted) {
            bindingResult.rejectValue("password", "user.password","An account already exists for this password");
            Log.logger.info(userExistMessage);
            Sentry.captureMessage(userExistMessage);
        }
        if (bindingResult.hasFieldErrors() || isUsernameExisted || isPasswordExisted) {
            returnedPage = "authorization/signup";
        } else {
            String saveUserMessage = "Saving user with name: " + user.getUsername();
            Log.logger.info(saveUserMessage);
            Sentry.captureMessage(saveUserMessage);
            userRepository.saveUser(user);
        }
        return returnedPage;
    }

    private boolean isUsernameExist(User user) {
        return userRepository.findUsersByUsername(user.getUsername()).size() > 0;
    }

    public boolean isPasswordExist(User userToSave) {
        return userRepository.findAll().stream().anyMatch(dbUser -> new BCryptPasswordEncoder()
                            .matches(userToSave.getPassword(), dbUser.getPassword()));
    }
}