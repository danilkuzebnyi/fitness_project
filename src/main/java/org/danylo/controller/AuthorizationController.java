package org.danylo.controller;

import org.danylo.model.User;
import org.danylo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Controller
public class AuthorizationController {
    UserService userService;

    @Autowired
    public AuthorizationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLogInForm(@ModelAttribute("user") User user) {
        return "authorization/login";
    }

    @GetMapping("/signup")
    public String getRegisterInForm(@ModelAttribute("user") User user) {
        return "authorization/signup";
    }

    @PostMapping("/signup")
    public String registerIn(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        return userService.save(user, bindingResult);
    }
}