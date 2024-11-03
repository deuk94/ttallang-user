package com.ttallang.user.userAuth.signup.service;


import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SignupService {
    String getAuthorizationUrl();
    ResponseEntity<Map<String, String>> getAccessToken(String code);
    ResponseEntity<Map<String, Object>> getUserInfo(String accessToken);
}
