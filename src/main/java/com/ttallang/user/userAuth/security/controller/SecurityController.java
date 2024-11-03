package com.ttallang.user.userAuth.security.controller;

import com.ttallang.user.userAuth.security.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/loginForm";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "userAuth/main/loginForm";
    }

    @GetMapping("/signupForm")
    public String signupForm(Model model) {
        model.addAttribute("customerName", "");
        model.addAttribute("email", "");
        return "userAuth/main/signupForm";
    }

    @GetMapping("/signupSelect")
    public String signupSelect() {
        return "userAuth/main/signupSelect";
    }
}
