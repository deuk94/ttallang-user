package com.ttallang.user.account.controller;

import com.ttallang.user.account.model.AccountResponse;
import com.ttallang.user.account.service.SignupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(response); // 성공했다면 콜백 url 로 가서 나머지 작업을 함.
    }

    @GetMapping("/signup/form/checkExisting/{userName}")
    public ResponseEntity<AccountResponse> checkExistingRolesUserName(@PathVariable String userName) {
        return signupService.isExistingRolesUserName(userName);
    }

    @PostMapping("/signup")
    public ResponseEntity<AccountResponse> signup(@RequestBody Map<String, String> userData) {
        try {
            return signupService.signupCustomer(userData);
        } catch (Exception e) { // 트랜잭션 어노테이션이 달려있으면 그 메서드 안에서는 try catch 로 안잡히고 자동으로 던져지는 것 같다...
            log.error("회원가입 실패... Exception={}", e.getMessage());
            return new ResponseEntity<>(new AccountResponse("guest", "회원가입 실패."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
