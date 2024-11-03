package com.ttallang.user.userAuth.login.controller;

import com.ttallang.user.userAuth.security.config.auth.PrincipalDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping(value = "/map/main")
    public String userMainPage(Model model) {
        try {
            PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("customerId", pds.getCustomerID());
            model.addAttribute("username", pds.getUsername());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "redirect:/loginForm";
        }
        return "userAuth/map/mymap";
    }
}
