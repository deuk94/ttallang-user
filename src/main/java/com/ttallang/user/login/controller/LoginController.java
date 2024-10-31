package com.ttallang.user.login.controller;

import com.ttallang.user.security.config.auth.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping(value = "/map/main")
    public String userMainPage(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        model.addAttribute("customerId", principalDetails.getCustomerID());
        return "map/mymap";
    }
}
