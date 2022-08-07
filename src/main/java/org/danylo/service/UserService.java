package org.danylo.service;

import org.danylo.logging.Log;
import org.danylo.model.User;
import org.danylo.repository.UserRepository;
import org.danylo.security.UserSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
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
        String returnedPage = "redirect:/login";
        if (isUsernameExist(user)) {
            bindingResult.rejectValue("username", "user.username","An account already exists for this email");
            Log.logger.info("User " + user.getUsername() + " exists");
        }
        if (bindingResult.hasFieldErrors() || isUsernameExist(user)) {
            returnedPage = "authorization/signup";
        } else {
            Log.logger.info("Saving user with name: " + user.getUsername());
            userRepository.saveUser(user);
        }
        return returnedPage;
    }

    private boolean isUsernameExist(User user) {
        return userRepository.findUsersByUsername(user.getUsername()).size() > 0;
    }
}