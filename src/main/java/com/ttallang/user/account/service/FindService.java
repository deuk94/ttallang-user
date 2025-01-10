package com.ttallang.user.account.service;

import com.ttallang.user.account.model.AccountResponse;
import com.ttallang.user.account.model.RolesUser;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.Map;

public interface FindService {
    ResponseEntity<AccountResponse> findUserName(Map<String, String> requestBody);
    ResponseEntity<AccountResponse> findPassword(Map<String, String> requestBody);
    boolean isAuthNumberStoredInSharedMapForFind(String to, String authNumber, Map<String, String> sharedMap) throws Exception;
    ResponseEntity<AccountResponse> getUserNameByCustomerPhone(Map<String, String> requestBody);
    ResponseEntity<AccountResponse> getPasswordByUserNameAndCustomerPhone(Map<String, String> requestBody);
    String renderPasswordChangePage(String state, Model model);
    ResponseEntity<AccountResponse> changePassword(Map<String, String> requestBody);
    ResponseEntity<AccountResponse> getResponseEntity(String customerPhone, RolesUser rolesUser, Map<String, String> sharedMap);
}
