package com.ttallang.user.security.service;


import com.ttallang.user.commonModel.User;
import com.ttallang.user.security.model.CertInfo;
import com.ttallang.user.security.response.SecurityResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SignupService {
    // 외부계정 회원가입.
    String getAuthorizationUrl(String SNSType);
    Map<String, Object> processSNSCert(Map<String, String> params);
    void unlinkUserCert(CertInfo certInfo);
    // 일반 회원가입.
    SecurityResponse isExistingRolesUserName(String userId);
    boolean isExistingCustomer(String email, String customerPhone);
    SecurityResponse signupCustomer(Map<String, String> userData);
}
