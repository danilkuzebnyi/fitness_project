package org.danylo.controller;

import org.danylo.dto.RecaptchaResponseDto;
import org.danylo.logging.Log;
import org.danylo.model.Country;
import org.danylo.model.User;
import org.danylo.service.CountryService;
import org.danylo.service.RecaptchaService;
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
import java.util.Map;

@Controller
public class AuthorizationController {
    private final UserService userService;
    private final CountryService countryService;
    private final RecaptchaService recaptchaService;

    @Autowired
    public AuthorizationController(UserService userService,
                                   CountryService countryService,
                                   RecaptchaService recaptchaService) {
        this.userService = userService;
        this.countryService = countryService;
        this.recaptchaService = recaptchaService;
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
                             @RequestParam("g-recaptcha-response") String recaptchaResponse,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession httpSession) {
        String returnedPage = "redirect:/login";

        Country selectedCountry = (Country) httpSession.getAttribute("selectedCountry");
        if (selectedCountry == null) {
            userService.rejectUserCountryValue(bindingResult);
        }

        boolean usernameExist = userService.isUsernameExist(user);
        if (usernameExist) {
            userService.rejectUsernameValue(bindingResult);
        }

        boolean recaptchaSuccess = recaptchaService.validateResponse(recaptchaResponse);
        if (!recaptchaSuccess) {

        }
        if (bindingResult.hasFieldErrors() || usernameExist || selectedCountry == null) {
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            returnedPage = "authorization/signup";
        } else {
            userService.save(user, httpSession);
        }
        return returnedPage;
    }

    @GetMapping("/activation/{code}")
    public String getActivationCode(@PathVariable String code) {
        userService.activateByCode(code);
        return "redirect:/login";
    }
}
