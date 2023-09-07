package org.danylo.controller;

import org.danylo.model.Country;
import org.danylo.model.User;
import org.danylo.service.CountryService;
import org.danylo.service.RecaptchaService;
import org.danylo.service.UserService;
import org.danylo.web.Dialog;
import org.danylo.web.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@Controller
public class AuthorizationController {
    private final UserService userService;
    private final CountryService countryService;
    private final RecaptchaService recaptchaService;
    private final HttpSession httpSession;
    private String sentConfirmationPassword = "";

    @Autowired
    public AuthorizationController(UserService userService,
                                   CountryService countryService,
                                   RecaptchaService recaptchaService,
                                   HttpSession httpSession) {
        this.userService = userService;
        this.countryService = countryService;
        this.recaptchaService = recaptchaService;
        this.httpSession = httpSession;
    }

    @GetMapping("/login")
    public String getLogInForm(@ModelAttribute("user") User user, Model model) {
        if (httpSession.getAttribute("activationLink") != null) {
            Dialog.create(model, Message.CHECK_EMAIL);
            httpSession.removeAttribute("activationLink");
        }
        return "authorization/login";
    }

    @GetMapping("/signup")
    public ModelAndView getRegisterInForm(@ModelAttribute("user") User user,
                                          @RequestParam(required = false) Integer countryId) {
        Country country = null;
        if (countryId != null) {
            country = countryService.getById(countryId);
        }
        countryService.setCountryFieldsToHttpSession(country, httpSession);
        httpSession.setAttribute("recaptchaSiteKey", recaptchaService.getSiteKey());
        httpSession.removeAttribute("isConfirmationMessageSent");

        return new ModelAndView("authorization/signup");
    }

    @PostMapping("/signup")
    public String registerIn(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult,
                             @RequestParam("g-recaptcha-response") String recaptchaResponse,
                             Model model) {
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
            model.addAttribute("recaptchaError", "");
            if (httpSession.getAttribute("isConfirmationMessageSent") == null) {
                sentConfirmationPassword = UUID.randomUUID().toString().substring(0, 12);
                userService.sendConfirmationMessageToEmail(user, sentConfirmationPassword);
                httpSession.setAttribute("isConfirmationMessageSent", true);
            }
            if (user.getConfirmationPassword() == null || !user.getConfirmationPassword().equals(sentConfirmationPassword)) {
                model.addAttribute("confirmationPasswordError", "");
                return "authorization/signup";
            }
        }

        if (bindingResult.hasFieldErrors() || usernameExist || selectedCountry == null) {
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "authorization/signup";
        }

        userService.save(user);
        httpSession.setAttribute("activationLink", "");
        return "redirect:/login";
    }

    @GetMapping("/activation/{code}")
    public String getActivationCode(@PathVariable String code) {
        userService.activateByCode(code);
        return "redirect:/login";
    }
}
