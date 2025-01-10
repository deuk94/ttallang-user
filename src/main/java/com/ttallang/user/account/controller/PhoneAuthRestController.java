package com.ttallang.user.account.controller;

import com.ttallang.user.account.model.AccountResponse;
import com.ttallang.user.account.service.PhoneAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class PhoneAuthRestController {

    private final PhoneAuthService phoneAuthService;

    public PhoneAuthRestController(PhoneAuthService phoneAuthService) {
        this.phoneAuthService = phoneAuthService;
    }

    @PostMapping("/phoneAuth")
    public ResponseEntity<AccountResponse> phoneAuth(@RequestBody Map<String, String> requestBody) {
        return phoneAuthService.startPhoneAuth(requestBody);
    }

    @PostMapping("/phoneAuth/result")
    public ResponseEntity<AccountResponse> phoneAuthCheck(@RequestBody Map<String, String> requestBody) {
        return phoneAuthService.getPhoneAuthResult(requestBody);
    }
}
