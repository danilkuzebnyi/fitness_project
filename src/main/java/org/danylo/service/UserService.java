package org.danylo.service;

import org.danylo.logging.Log;
import org.danylo.model.Country;
import org.danylo.model.User;
import org.danylo.repository.UserRepository;
import org.danylo.security.UserSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    UserRepository userRepository;
    CountryService countryService;

    @Autowired
    public UserService(UserRepository userRepository, CountryService countryService) {
        this.userRepository = userRepository;
        this.countryService = countryService;
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
            userRepository.saveUser(user);
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

    private boolean isUsernameExist(User user) {
        return userRepository.findUsersByUsername(user.getUsername()).size() > 0;
    }
}