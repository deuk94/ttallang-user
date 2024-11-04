package com.ttallang.user.security.controller;

import com.ttallang.user.security.service.SignupService;
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

    // SNS 계정 가입 및 일반가입 선택하는 페이지.
    @GetMapping("/signup/select")
    public String signupSelect() {
        return "userAuth/main/signupSelect";
    }

    // 인증 방식을 정하는 페이지.
    @GetMapping("/signup/auth")
    public String signupAuth() {
        return "userAuth/main/signupAuth";
    }

    @GetMapping("/signup/form")
    public String signupForm(Model model) {
        model.addAttribute("customerName", "");
        model.addAttribute("email", "");
        return "userAuth/main/signupForm";
    }

    @GetMapping("/signup/form/admin")
    public String signupAdminForm(Model model) {
        return "userAuth/main/signupAdminForm";
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
            Map<String, Map<String, String>> data = (Map<String, Map<String, String>>) resultBody.get("data");
            Map<String, String> member = (Map<String, String>) data.get("member");

//            유저 개인정보
//            userInfo.put("name", (String) member.get("name"));
//            userInfo.put("email", (String) member.get("email"));
//            userInfo.put("birthday", (String) member.get("birthday"));
//            userInfo.put("mobile", (String) member.get("mobile"));

            model.addAttribute("customerName", (String) member.get("name"));
            model.addAttribute("email", (String) member.get("email"));
            return "userAuth/main/signupForm";
        } else {
//            throw new RuntimeException("유저 정보 가져오기 실패...");
            return "redirect:/login/form";
        }
    }

}
