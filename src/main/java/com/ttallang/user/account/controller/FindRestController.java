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
        AccountResponse accountResponse = new AccountResponse("guest", null);
        String customerPhone = requestBody.get("customerPhone");
        try {
            if (findService.findUserNameByCustomerPhone(customerPhone)) {
                int sendSMSResult = findService.sendSMS(customerPhone);
                if (sendSMSResult == 0) { // 보내기 시도.
                    accountResponse.setMessage("이미 인증이 진행중입니다.");
                    return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST);
                } else if (sendSMSResult == -1) {
                    accountResponse.setMessage("인증 서비스의 잔액이 부족하여 인증이 취소되었습니다.\n관리자에게 문의해주세요.");
                    sharedAuthNumberMap.remove(customerPhone); // 잔액 부족으로 인해 인증이 취소되었으므로 인증정보를 지운다.
                    return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                accountResponse.setMessage("일치하는 유저 정보가 없습니다.");
                sharedAuthNumberMap.remove(customerPhone); // 일치하는 유저 정보가 없어서 인증이 의미없으므로 인증정보를 지운다.
                return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            accountResponse.setMessage("인증 도중 에러가 발생하였습니다.");
            log.error("유저 정보 찾기 에러: {}", e.getMessage());
            sharedAuthNumberMap.remove(customerPhone); // 에러로 인해 인증 취소되었으므로 인증정보를 지운다.
            return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        accountResponse.setMessage("전송 성공.");
        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
    }

    @PostMapping("/find/username/auth")
    public ResponseEntity<AccountResponse> checkAuthNumber(@RequestBody Map<String, String> requestBody) {
        AccountResponse accountResponse = new AccountResponse("guest", "인증 번호가 다릅니다.");
        String customerPhone = requestBody.get("customerPhone");
        String authNumber1 = requestBody.get("authNumber"); // 입력받은 인증번호.
        try {
            String authNumber2 = sharedAuthNumberMap.get(customerPhone); // 공유메모리에 저장돼있던 대조용 인증번호.
            if (authNumber1.equals(authNumber2)) { // 인증번호가 일치한다면,
                RolesUser rolesUser = findService.getUserNameByCustomerPhone(customerPhone);
                accountResponse.setMessage(rolesUser.getUserName());
                sharedAuthNumberMap.remove(customerPhone); // 임시 인증정보를 지운다.
                return new ResponseEntity<>(accountResponse, HttpStatus.OK); // 200.
            } else {
                return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST); // 200.
            }
        } catch (Exception e) {
            accountResponse.setMessage("인증 도중 에러가 발생하였습니다.");
            log.error("SMS 인증 에러: {}", e.getMessage());
            return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR); // 500.
        }
    }
}
