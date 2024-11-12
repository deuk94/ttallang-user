package com.ttallang.user.account.controller;

import com.ttallang.user.account.model.RolesUser;
import com.ttallang.user.account.model.AccountResponse;
import com.ttallang.user.account.service.FindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class FindRestController {

    private final FindService findService;
    private final Map<String, String> sharedAuthNumberMap;

    public FindRestController(FindService findService, Map<String, String> sharedAuthNumberMap) {
        this.findService = findService;
        this.sharedAuthNumberMap = sharedAuthNumberMap;
    }

    @PostMapping("/find/username")
    public ResponseEntity<AccountResponse> findUserNameByCustomerPhone(@RequestBody Map<String, String> requestBody) {
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setRole("guest");
        String customerPhone = requestBody.get("customerPhone");
        System.out.println(customerPhone);
        try {
            if (findService.findUserNameByCustomerPhone(customerPhone)) {
                if (!findService.sendSms(customerPhone)) { // 보내기 시도.
                    accountResponse.setMessage("이미 인증이 진행중입니다.");
                    return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST);
                }; 
            } else {
                accountResponse.setMessage("일치하는 유저 정보가 없습니다.");
                return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            accountResponse.setMessage("인증 도중 에러가 발생하였습니다.");
            log.error("유저 정보 찾기 에러: {}", e.getMessage());
            return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        accountResponse.setMessage("보내기 성공.");
        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
    }

    @PostMapping("/find/username/auth")
    public ResponseEntity<AccountResponse> checkAuthNumber(@RequestBody Map<String, String> requestBody) {
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setRole("guest");
        accountResponse.setMessage("인증 실패.");
        System.out.println(requestBody);
        String customerPhone = requestBody.get("customerPhone");
        String number1 = requestBody.get("authNumber");
        try {
            String number2 = sharedAuthNumberMap.get(customerPhone);
            if (number1.equals(number2)) { // 인증번호가 일치한다면,
                RolesUser rolesUser = findService.getUserNameByCustomerPhone(customerPhone);
                accountResponse.setMessage(rolesUser.getUserName());
                sharedAuthNumberMap.remove(customerPhone); // 임시 인증정보를 지운다.
                return new ResponseEntity<>(accountResponse, HttpStatus.OK); // 200.
            }
        } catch (Exception e) {
            accountResponse.setMessage("인증 도중 에러가 발생하였습니다.");
            log.error("SMS 인증 에러: {}", e.getMessage());
            return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR); // 500.
        }
        return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST); // 400.
    }
}
