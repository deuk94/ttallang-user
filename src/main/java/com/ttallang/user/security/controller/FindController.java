package com.ttallang.user.security.controller;

import com.ttallang.user.security.service.FindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
public class FindController {

    private final FindService findService;

    public FindController(FindService findService) {
        this.findService = findService;
    }

    @GetMapping("/find/select")
    public String find() {
        return "userAuth/main/findSelect";
    }

    @GetMapping("/find/username")
    public String inputCustomerPhone() {
        return "userAuth/main/inputCustomerPhone";
    }

    @GetMapping("/find/userName/result")
    public String findUserNameByCustomerPhone(String customerPhone) {
        try {
            if (findService.findUserNameByCustomerPhone(customerPhone)) {
                findService.sendSms(customerPhone);
            } else {
                String encodedMessage = URLEncoder.encode("일치하는 유저 정보가 없습니다.", StandardCharsets.UTF_8);
                return "redirect:/login/form?error="+encodedMessage;
            }
        } catch (Exception e) {
            log.error("error={}", e.getMessage());
            String encodedMessage = URLEncoder.encode("인증 도중 에러가 발생하였습니다.", StandardCharsets.UTF_8);
            return "redirect:/login/form?error="+encodedMessage;
        }
        return "redirect:/login/form";
    }
}
