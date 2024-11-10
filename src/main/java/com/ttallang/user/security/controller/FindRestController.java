package com.ttallang.user.security.controller;

import com.ttallang.user.commonModel.User;
import com.ttallang.user.security.model.CertInfo;
import com.ttallang.user.security.model.RolesUser;
import com.ttallang.user.security.response.SecurityResponse;
import com.ttallang.user.security.service.FindService;
import com.ttallang.user.security.service.FindServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public SecurityResponse findUserNameByCustomerPhone(@RequestBody Map<String, String> requestBody) {
        SecurityResponse securityResponse = new SecurityResponse();
        securityResponse.setCode(401);
        securityResponse.setStatus("failure");
        securityResponse.setRole("guest");
        String customerPhone = requestBody.get("customerPhone");
        System.out.println(customerPhone);
        try {
            if (findService.findUserNameByCustomerPhone(customerPhone)) {
                if (!findService.sendSms(customerPhone)) { // 보내기 시도.
                    securityResponse.setMessage("이미 인증이 진행중입니다.");
                    return securityResponse;
                }; 
            } else {
                securityResponse.setMessage("일치하는 유저 정보가 없습니다.");
                return securityResponse;
            }
        } catch (Exception e) {
            securityResponse.setCode(500);
            securityResponse.setMessage("인증 도중 에러가 발생하였습니다.");
            log.error("유저 정보 찾기 에러: {}", e.getMessage());
            return securityResponse;
        }
        securityResponse.setCode(200);
        securityResponse.setStatus("success");
        securityResponse.setRole("guest");
        securityResponse.setMessage("보내기 성공.");
        return securityResponse;
    }

    @PostMapping("/find/username/auth")
    public SecurityResponse checkAuthNumber(@RequestBody Map<String, String> requestBody) {
        SecurityResponse securityResponse = new SecurityResponse();
        securityResponse.setCode(401);
        securityResponse.setStatus("failure");
        securityResponse.setRole("guest");
        securityResponse.setMessage("인증 실패.");
        String customerPhone = requestBody.get("customerPhone");
        String number1 = requestBody.get("authNumber");
        try {
            String number2 = sharedAuthNumberMap.get(customerPhone);
            if (number1.equals(number2)) {
                RolesUser rolesUser = findService.getUserNameByCustomerPhone(customerPhone);
                securityResponse.setCode(200);
                securityResponse.setStatus("success");
                securityResponse.setMessage(rolesUser.getUserName());
                sharedAuthNumberMap.remove(customerPhone); // 임시 정보를 지운다.
                return securityResponse;
            }
        } catch (Exception e) {
            securityResponse.setCode(500);
            securityResponse.setMessage("인증 도중 에러가 발생하였습니다.");
            log.error("SMS 인증 에러: {}", e.getMessage());
            return securityResponse;
        }
        return securityResponse;
    }
}
