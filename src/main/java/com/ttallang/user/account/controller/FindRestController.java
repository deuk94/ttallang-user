package com.ttallang.user.account.controller;

import com.ttallang.user.account.model.RolesUser;
import com.ttallang.user.account.model.AccountResponse;
import com.ttallang.user.account.service.FindService;
import lombok.extern.slf4j.Slf4j;
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
    public AccountResponse findUserNameByCustomerPhone(@RequestBody Map<String, String> requestBody) {
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setCode(401);
        accountResponse.setStatus("failure");
        accountResponse.setRole("guest");
        String customerPhone = requestBody.get("customerPhone");
        System.out.println(customerPhone);
        try {
            if (findService.findUserNameByCustomerPhone(customerPhone)) {
                if (!findService.sendSms(customerPhone)) { // 보내기 시도.
                    accountResponse.setMessage("이미 인증이 진행중입니다.");
                    return accountResponse;
                }; 
            } else {
                accountResponse.setMessage("일치하는 유저 정보가 없습니다.");
                return accountResponse;
            }
        } catch (Exception e) {
            accountResponse.setCode(500);
            accountResponse.setMessage("인증 도중 에러가 발생하였습니다.");
            log.error("유저 정보 찾기 에러: {}", e.getMessage());
            return accountResponse;
        }
        accountResponse.setCode(200);
        accountResponse.setStatus("success");
        accountResponse.setRole("guest");
        accountResponse.setMessage("보내기 성공.");
        return accountResponse;
    }

    @PostMapping("/find/username/auth")
    public AccountResponse checkAuthNumber(@RequestBody Map<String, String> requestBody) {
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setCode(401);
        accountResponse.setStatus("failure");
        accountResponse.setRole("guest");
        accountResponse.setMessage("인증 실패.");
        String customerPhone = requestBody.get("customerPhone");
        String number1 = requestBody.get("authNumber");
        try {
            String number2 = sharedAuthNumberMap.get(customerPhone);
            if (number1.equals(number2)) {
                RolesUser rolesUser = findService.getUserNameByCustomerPhone(customerPhone);
                accountResponse.setCode(200);
                accountResponse.setStatus("success");
                accountResponse.setMessage(rolesUser.getUserName());
                sharedAuthNumberMap.remove(customerPhone); // 임시 정보를 지운다.
                return accountResponse;
            }
        } catch (Exception e) {
            accountResponse.setCode(500);
            accountResponse.setMessage("인증 도중 에러가 발생하였습니다.");
            log.error("SMS 인증 에러: {}", e.getMessage());
            return accountResponse;
        }
        return accountResponse;
    }
}
