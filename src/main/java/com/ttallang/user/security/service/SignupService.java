package com.ttallang.user.security.service;


import com.ttallang.user.security.model.CertInfo;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SignupService {
    // 외부계정 회원가입.
    String getAuthorizationUrl(String SNSType);
    ResponseEntity<Map<String, String>> getAccessToken(String code, String SNSType);
    ResponseEntity<Map<String, Object>> getUserInfo(String accessToken, String SNSType);
    ResponseEntity<Map<String, Object>> unlinkUserCert(CertInfo certInfo);
    // 일반 회원가입.
    boolean isExistingCustomer(String userName);
    void signupCustomer(Map<String, String> userData);
}
