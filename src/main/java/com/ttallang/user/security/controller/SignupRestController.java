package com.ttallang.user.security.controller;

import com.ttallang.user.security.model.CertInfo;
import com.ttallang.user.security.response.SecurityResponse;
import com.ttallang.user.security.service.SignupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class SignupRestController { // 리턴 타입이 JSON인 컨트롤러.

    private final SignupService signupService;

    public SignupRestController(SignupService signupService) {
        this.signupService = signupService;
    }

    @GetMapping("/oauth2/{SNSType}")
    public ResponseEntity<String> oAuth2Login(@PathVariable("SNSType") String SNSType) {
        String response = signupService.getAuthorizationUrl(SNSType);
        try {
            assert response != null;
        } catch (Exception e) {
            log.error("로그인창에 진입할 수 없습니다. 원인={}", e.getMessage());
            return ResponseEntity.ok("/login/form");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/signup/form/checkExisting/{userName}")
    public SecurityResponse checkExistingRolesUserName(@PathVariable String userName) {
        return signupService.isExistingRolesUserName(userName);
    }

    @PostMapping("/signup")
    public SecurityResponse signup(@RequestBody Map<String, String> userData) {
        return signupService.signupCustomer(userData);
    }
}
