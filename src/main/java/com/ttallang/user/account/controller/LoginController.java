package com.ttallang.user.account.controller;

import com.ttallang.user.security.config.auth.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class LoginController {

    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/login/form";
    }

    @GetMapping("/login/form")
    public String loginForm() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user instanceof PrincipalDetails) {
            return "redirect:/main";
        }
        return "account/login/form";
    }

}
