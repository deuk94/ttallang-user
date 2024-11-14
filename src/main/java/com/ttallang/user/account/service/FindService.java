package com.ttallang.user.account.service;

import com.ttallang.user.account.model.AccountResponse;
import com.ttallang.user.account.model.RolesUser;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface FindService {
    ResponseEntity<AccountResponse> findUserName(Map<String, String> requestBody);
    ResponseEntity<AccountResponse> findPassword(Map<String, String> requestBody);
    ResponseEntity<AccountResponse> checkAuthNumber(Map<String, String> requestBody, String findType);
//    ResponseEntity<AccountResponse> changePassword(String stateCode);
}
