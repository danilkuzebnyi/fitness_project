package org.danylo.controller;

import org.danylo.service.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;

@Controller
public class SupportController {
    private final SupportService supportService;

    @Autowired
    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    @RequestMapping(value = "/support", method = {RequestMethod.GET, RequestMethod.POST})
    public String requestCall(@ModelAttribute("telephoneNumber") String telephoneNumber,
                              HttpServletRequest request) {
        String previousPage = request.getHeader("Referer");
        supportService.callToNumber(telephoneNumber);
        return "redirect:" + previousPage;
    }
}
