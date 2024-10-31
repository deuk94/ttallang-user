package com.ttallang.user.test.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ttallang.user.security.config.auth.PrincipalDetails;

@Controller
@RequestMapping("/test")
public class TestController {
    @GetMapping("/myPage")
    public String myPage(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        model.addAttribute("customerId", principalDetails.getCustomerID());
        return "test/myPage";
    }
}
