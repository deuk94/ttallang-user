package com.ttallang.user.security.controller;

import com.ttallang.user.security.response.SecurityResponse;
import com.ttallang.user.security.service.SignupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class SignupRestController {
    // 리턴 타입이 JSON인 컨트롤러.

    private final SignupService signupService;

    public SignupRestController(SignupService signupService) {
        this.signupService = signupService;
    }

    @GetMapping("/oauth2/{SNSType}")
    public ResponseEntity<String> oAuth2Login(@PathVariable("SNSType") String SNSType) {
        System.out.println("로그인창 진입...");
        String response = signupService.getAuthorizationUrl(SNSType);
        try {
            assert response != null;
        } catch (Exception e) {
            log.error("로그인창에 진입할 수 없습니다. 원인={}", e.getMessage());
            return ResponseEntity.ok("/login/form");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup/form/checkExisting")
    public SecurityResponse checkExisting(@RequestBody Map<String, String> userId) {
        String userName = userId.get("username");
        SecurityResponse securityResponse = new SecurityResponse();
        securityResponse.setRole("guest");
        securityResponse.setStatus("success");
        if (!signupService.isExistingCustomer(userName)) {
            securityResponse.setCode(204);
            securityResponse.setMessage("가입 가능한 ID.");
        } else {
            securityResponse.setCode(200);
            securityResponse.setMessage("이미 존재하는 ID.");
        }
        return securityResponse;
    }

    @PostMapping("/signup")
    public SecurityResponse signup(@RequestBody Map<String, String> userData) {
        SecurityResponse securityResponse = new SecurityResponse();
        try {
            signupService.signupCustomer(userData);
            securityResponse.setCode(200);
            securityResponse.setStatus("success");
            securityResponse.setRole("guest");
            securityResponse.setMessage("회원가입 성공.");
        } catch (Exception e) {
            securityResponse.setCode(500);
            securityResponse.setStatus("failure");
            securityResponse.setRole("guest");
            securityResponse.setMessage("회원가입 실패,"+e.getMessage());
            log.error("회원가입에 실패했습니다. 원인={}", e.getMessage());
        }
        return securityResponse;
    }
}
