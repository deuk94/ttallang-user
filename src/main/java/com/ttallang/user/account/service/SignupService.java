package com.ttallang.user.account.service;

import com.ttallang.user.account.model.CertInfo;
import com.ttallang.user.account.model.AccountResponse;
import org.springframework.ui.Model;

import java.util.Map;

public interface SignupService {
    // 외부계정 회원가입.
    String getAuthorizationUrl(String SNSType);
    Map<String, Object> processSNSCert(Map<String, String> params);
    // 일반 회원가입.
    AccountResponse isExistingRolesUserName(String userId);
    AccountResponse signupCustomer(Map<String, String> userData);
    String fillOutSignupForm(Map<String, Object> responseBody, String SNSType, CertInfo certInfo, Model model);
}
