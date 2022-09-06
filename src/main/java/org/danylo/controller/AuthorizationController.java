package org.danylo.controller;

import org.danylo.model.Country;
import org.danylo.model.User;
import org.danylo.service.CountryService;
import org.danylo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class AuthorizationController {
    UserService userService;
    CountryService countryService;

    @Autowired
    public AuthorizationController(UserService userService, CountryService countryService) {
        this.userService = userService;
        this.countryService = countryService;
    }

    @GetMapping("/login")
    public String getLogInForm(@ModelAttribute("user") User user) {
        return "authorization/login";
    }

    @GetMapping("/signup")
    public ModelAndView getRegisterInForm(@ModelAttribute("user") User user,
                                          @RequestParam(required = false) Integer countryId,
                                          HttpSession httpSession) {
        List<Country> countries = countryService.getAll();
        Country country = null;
        if (countryId != null) {
            country = countryService.getById(countryId);
        }
        String code = country == null ? "" : country.getCode();
        httpSession.setAttribute("countries", countries);
        httpSession.setAttribute("selectedCountry", country);
        httpSession.setAttribute("code", code);
        return new ModelAndView("authorization/signup");
    }

    @PostMapping("/signup")
    public String registerIn(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession httpSession) {
        return userService.save(user, bindingResult, model, httpSession);
    }

    @GetMapping("/activation/{code}")
    public String getActivationCode(@PathVariable String code) {
        userService.activateByCode(code);
        return "redirect:/login";
    }
}
