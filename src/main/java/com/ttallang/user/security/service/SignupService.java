package com.ttallang.user.security.service;


import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SignupService {
    // 외부계정 회원가입.
    String getAuthorizationUrl();
    ResponseEntity<Map<String, String>> getAccessToken(String code);
    ResponseEntity<Map<String, Object>> getUserInfo(String accessToken);
    // 일반 회원가입.
    boolean isExistingCustomer(String userName);
    void signupCustomer(Map<String, String> userData);
    void signupAdmin(Map<String, String> userData);
}
