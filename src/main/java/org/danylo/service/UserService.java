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
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final CountryService countryService;
    private final MailSender mailSender;
    private final HttpSession httpSession;

    @Value("${app.url}")
    private String url;

    @Autowired
    public UserService(UserRepository userRepository,
                       CountryService countryService,
                       MailSender mailSender,
                       HttpSession httpSession) {
        this.userRepository = userRepository;
        this.countryService = countryService;
        this.mailSender = mailSender;
        this.httpSession = httpSession;
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

    public void save(User user) {
        Log.logger.info("Saving user with name: " + user.getUsername());
        user.setCountry((Country) httpSession.getAttribute("selectedCountry"));
        user.setActivationCode(UUID.randomUUID().toString());
        userRepository.saveUser(user);
        sendActivationMessageToEmail(user);
    }

    public void rejectUsernameValue(BindingResult bindingResult) {
        bindingResult.rejectValue("username", "user.username","An account already exists for this email");
    }

    public boolean isUsernameExist(User user) {
        return userRepository.findUsersByUsername(user.getUsername()).size() > 0;
    }

    public void rejectUserCountryValue(BindingResult bindingResult) {
        bindingResult.rejectValue("country", "user.country","Please select your country");
    }

    public void setUserDataInProfile(User currentUser, Integer countryId, HttpSession httpSession) {
        Country selectedCountry = countryId == null ? currentUser.getCountry() : countryService.getById(countryId);
        countryService.setCountryFieldsToHttpSession(selectedCountry, httpSession);
        String code = selectedCountry == null ? "" : selectedCountry.getCode();
        currentUser.setTelephoneNumber(currentUser.getTelephoneNumber().substring(code.length()));
        httpSession.setAttribute("currentUser", currentUser);
    }

    public void sendConfirmationMessageToEmail(User user, String confirmationPassword) {
        String message = String.format("Hello, %s! \n" +
                        "This your one-time password: %s",
                user.getFirstName(), confirmationPassword);
        mailSender.send(user.getUsername(), "D-Fitness", message);
    }

    public void activateByCode(String code) {
        User user = userRepository.findByActivationCode(code);
        if (user != null) {
            user.setStatus(Status.ACTIVE);
            userRepository.updateUserStatus(user);
        }
    }

    private void sendActivationMessageToEmail(User user) {
        String message = String.format("Hello, %s! \n" +
                        "Click here to activate your account %s/activation/%s",
                 user.getFirstName(), url, user.getActivationCode());
        mailSender.send(user.getUsername(), "D-Fitness", message);
    }
}