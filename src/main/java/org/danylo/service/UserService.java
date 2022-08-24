package org.danylo.service;

import org.danylo.logging.Log;
import org.danylo.model.Country;
import org.danylo.model.Status;
import org.danylo.model.User;
import org.danylo.repository.UserRepository;
import org.danylo.security.UserSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final CountryService countryService;
    private final MailSender mailSender;

    @Value("${app.url}")
    private String url;

    @Autowired
    public UserService(UserRepository userRepository,
                       CountryService countryService,
                       MailSender mailSender) {
        this.userRepository = userRepository;
        this.countryService = countryService;
        this.mailSender = mailSender;
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

    public String save(User user, BindingResult bindingResult, HttpSession httpSession) {
        String returnedPage = "redirect:/login";
        if (isUsernameExist(user)) {
            bindingResult.rejectValue("username", "user.username","An account already exists for this email");
            Log.logger.info("User " + user.getUsername() + " exists");
        }
        if (bindingResult.hasFieldErrors() || isUsernameExist(user)) {
            returnedPage = "authorization/signup";
        } else {
            Log.logger.info("Saving user with name: " + user.getUsername());
            user.setCountry((Country) httpSession.getAttribute("selectedCountry"));
            user.setActivationCode(UUID.randomUUID().toString());
            userRepository.saveUser(user);
            sendMessageToEmail(user);
        }
        return returnedPage;
    }

    public void setUserDataInProfile(User currentUser, Integer countryId, HttpSession httpSession) {
        List<Country> countries = countryService.getAll();
        Country selectedCountry = countryId == null ? currentUser.getCountry() : countryService.getById(countryId);
        String code = selectedCountry == null ? "" : selectedCountry.getCode();
        currentUser.setTelephoneNumber(currentUser.getTelephoneNumber().substring(code.length()));

        httpSession.setAttribute("selectedCountry", selectedCountry);
        httpSession.setAttribute("currentUser", currentUser);
        httpSession.setAttribute("countries", countries);
        httpSession.setAttribute("code", code);
    }

    public void activateByCode(String code) {
        User user = userRepository.findByActivationCode(code);
        if (user != null) {
            user.setStatus(Status.ACTIVE);
            userRepository.updateUserStatus(user);
        }
    }

    private void sendMessageToEmail(User user) {
        String message = String.format("Hello, %s! \n" +
                        "Click here to activate your account %s/activation/%s",
                 user.getFirstName(), url, user.getActivationCode());
        mailSender.send(user.getUsername(), "D-Fitness", message);
    }

    private boolean isUsernameExist(User user) {
        return userRepository.findUsersByUsername(user.getUsername()).size() > 0;
    }
}