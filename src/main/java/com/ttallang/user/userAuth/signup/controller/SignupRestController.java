package com.ttallang.user.userAuth.signup.controller;

import com.ttallang.user.userAuth.signup.service.SignupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oauth2")
public class SignupRestController {
    private final SignupService signupService;

    public SignupRestController(SignupService signupService) {
        this.signupService = signupService;
    }

    @GetMapping("/payco")
    public ResponseEntity<String> paycoLogin() {
        System.out.println("로그인창 진입...");
        String responseEntity = signupService.getAuthorizationUrl();
        return ResponseEntity.ok(responseEntity);
    }
}
