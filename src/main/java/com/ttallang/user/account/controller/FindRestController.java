package com.ttallang.user.account.controller;

import com.ttallang.user.account.model.AccountResponse;
import com.ttallang.user.account.service.FindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class FindRestController {

    private final FindService findService;

    public FindRestController(FindService findService) {
        this.findService = findService;
    }

    @PostMapping("/find/userName")
    public ResponseEntity<AccountResponse> findUserName(@RequestBody Map<String, String> requestBody) {
        return findService.findUserName(requestBody);
    }

    @PostMapping("/find/password")
    public ResponseEntity<AccountResponse> findPassword(@RequestBody Map<String, String> requestBody) {
        return findService.findPassword(requestBody);
    }

    @PostMapping("/find/userName/auth")
    public ResponseEntity<AccountResponse> checkUsernameAuthNumber(@RequestBody Map<String, String> requestBody) {
        return findService.checkAuthNumber(requestBody, "userName");
    }

    @PostMapping("/find/password/auth")
    public ResponseEntity<AccountResponse> checkPasswordAuthNumber(@RequestBody Map<String, String> requestBody) {
        return findService.checkAuthNumber(requestBody, "password");
    }

//    @GetMapping("/find/username/{stateCode}/changePassword")
//    public ResponseEntity<AccountResponse> changePassword(@PathVariable String stateCode) {
//        return findService.changePassword(stateCode);
//    }
}
