package com.ttallang.user.security.controller;

import com.ttallang.user.security.response.SecurityResponse;
import com.ttallang.user.security.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/intro";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "main/loginForm";
    }

    @GetMapping("/signupForm")
    public String signupForm() {
        return "main/signupForm";
    }

    @PostMapping("/signupForm/checkExisting")
    @ResponseBody
    public SecurityResponse checkExisting(@RequestBody Map<String, String> userId) {
        String userName = userId.get("username");
        SecurityResponse securityResponse = new SecurityResponse();
        securityResponse.setRole("guest");
        securityResponse.setStatus("success");
        if (!securityService.isExistingCustomer(userName)) {
            securityResponse.setCode(204);
            securityResponse.setMessage("가입 가능한 ID.");
        } else {
            securityResponse.setCode(200);
            securityResponse.setMessage("이미 존재하는 ID.");
        }
        return securityResponse;
    }

    @PostMapping("/signup")
    @ResponseBody
    public SecurityResponse signup(@RequestBody Map<String, String> userData) {
        SecurityResponse securityResponse = new SecurityResponse();
        try {
            securityService.signupCustomer(userData);
            securityResponse.setCode(200);
            securityResponse.setStatus("success");
            securityResponse.setRole("guest");
            securityResponse.setMessage("회원가입 성공.");
        } catch (Exception e) {
            securityResponse.setCode(500);
            securityResponse.setStatus("failure");
            securityResponse.setRole("guest");
            securityResponse.setMessage("회원가입 실패,"+e.getMessage());
            System.out.println("예외 발생: " + e.getMessage());
        }
        return securityResponse;
    }
}
