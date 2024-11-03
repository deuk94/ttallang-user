package com.ttallang.user.userAuth.signup.controller;

import com.ttallang.user.userAuth.signup.service.SignupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class SignupController {

    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @GetMapping("/signupAuth")
    public String signupAuth() {
        return "userAuth/main/signupAuth";
    }

    @GetMapping("/api/oauth2/payco/callback")
    public String paycoCallback(@RequestParam("code") String code, Model model) {
        log.info("코드 받기 성공={}", code);
        // 인증 코드로 토큰 받기
        ResponseEntity<Map<String, String>> accessTokenResponse = signupService.getAccessToken(code);
        log.info("토큰 응답 받기 성공={}", accessTokenResponse);
        Map<String, String> responseBody = accessTokenResponse.getBody();

        if (responseBody != null) {
            String accessToken = responseBody.get("access_token");
            log.info("토큰 받기 성공={}", accessToken);
            // 토큰으로 사용자 정보 조회.
            ResponseEntity<Map<String, Object>> result = signupService.getUserInfo(accessToken);
            log.info("result={}", result);
            Map<String, Object> resultBody = result.getBody();
            Map<String, String> userInfo = new HashMap<>();
            Map<String, Map<String, String>> data = (Map) resultBody.get("data");
            Map<String, String> member = (Map) data.get("member");
            userInfo.put("id", (String) member.get("id"));
            userInfo.put("name", (String) member.get("name"));
            userInfo.put("email", (String) member.get("email"));
            userInfo.put("birthdayMMdd", (String) member.get("birthdayMMdd"));
            userInfo.put("birthday", (String) member.get("birthday"));
            userInfo.put("mobile", (String) member.get("mobile"));
            userInfo.put("ci", (String) member.get("ci"));
            model.addAttribute("userInfo", userInfo);
            model.addAttribute("customerName", (String) member.get("name"));
            model.addAttribute("email", (String) member.get("email"));
            return "userAuth/main/signupForm";
        } else {
            throw new RuntimeException("유저 정보 가져오기 실패...");
        }
    }

}
