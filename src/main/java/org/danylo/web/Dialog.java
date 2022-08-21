package org.danylo.web;

import org.springframework.ui.Model;

public class Dialog {
    public static void create(Model model, Message message) {
        model.addAttribute("message", message);
    }
}
