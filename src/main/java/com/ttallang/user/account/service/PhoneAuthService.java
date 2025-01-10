package com.ttallang.user.account.service;

import com.ttallang.user.account.model.AccountResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface PhoneAuthService {
    boolean isAuthNumberStoredInSharedMap(String to, String authNumber, Map<String, String> sharedMap) throws Exception;
    boolean isSMSSentSuccess(String to, String authNumber);
    boolean isCorrectAuthNumber(Map<String, String> requestBody, Map<String, String> sharedMap) throws Exception;
    ResponseEntity<AccountResponse> startPhoneAuth(Map<String, String> requestBody);
    ResponseEntity<AccountResponse> getPhoneAuthResult(Map<String, String> requestBody);
}
