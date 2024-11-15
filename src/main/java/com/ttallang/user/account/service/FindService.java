package com.ttallang.user.account.service;

import com.ttallang.user.account.model.AccountResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.Map;

public interface FindService {
    ResponseEntity<AccountResponse> findUserName(Map<String, String> requestBody);
    ResponseEntity<AccountResponse> findPassword(Map<String, String> requestBody);
    ResponseEntity<AccountResponse> checkAuthNumber(Map<String, String> requestBody, String findType);
    String renderPasswordChangePage(String state, Model model);
    ResponseEntity<AccountResponse> changePassword(Map<String, String> requestBody);
}
