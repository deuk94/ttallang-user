package com.ttallang.user.security.controller;

import com.ttallang.user.security.config.auth.PrincipalDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/login/form";
    }

    @GetMapping("/login/form")
    public String loginForm() {
        return "userAuth/main/loginForm";
    }

    @GetMapping(value = "/map/main")
    public String userMainPage(Model model) {
        try {
            PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("customerId", pds.getCustomerID());
            model.addAttribute("username", pds.getUsername());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "redirect:/login/form";
        }
        return "userAuth/map/mymap";
    }
}
