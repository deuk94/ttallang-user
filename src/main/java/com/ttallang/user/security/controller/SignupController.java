package com.ttallang.user.security.controller;

import com.ttallang.user.security.service.SignupService;
import jakarta.servlet.http.HttpServletRequest;
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
    // 리턴 타입이 JSP인 컨트롤러.

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

    @GetMapping("/api/oauth2/callback")
    public String paycoCallback(
            @RequestParam("code") String code,
            HttpServletRequest request,
            Model model
    ) {
        log.info("코드 받기 성공={}", code);
        String SNSType = null;
        String refererDomain = request.getHeader("Referer");
        log.info("refererDomain={}", refererDomain);
        ResponseEntity<Map<String, String>> responseEntity;
        if (refererDomain.contains("payco")) {
            SNSType = "payco";
        } else if (refererDomain.contains("kakao")) {
            SNSType = "kakao";
        } else if (refererDomain.contains("naver")) {
            SNSType = "naver";
        } else {
            throw new RuntimeException("SNS 타입이 지정되지 않았습니다.");
        }
        // 인증 코드로 토큰 받기
        ResponseEntity<Map<String, String>> accessTokenResponse = signupService.getAccessToken(code, SNSType);
        log.info("토큰 응답 받기 성공={}", accessTokenResponse);
        Map<String, String> responseBody = accessTokenResponse.getBody();

        assert responseBody != null;

        String accessToken = responseBody.get("access_token");
        log.info("토큰 받기 성공={}", accessToken);
        // 토큰으로 사용자 정보 조회.
        ResponseEntity<Map<String, Object>> result = signupService.getUserInfo(accessToken, SNSType);
        log.info("result={}", result);
        Map<String, Object> resultBody = result.getBody();
        if (SNSType.equals("payco")) {
            Map<String, Map<String, String>> data = (Map<String, Map<String, String>>) resultBody.get("data");
            Map<String, String> member = (Map<String, String>) data.get("member");
            model.addAttribute("customerName", (String) member.get("name"));
            model.addAttribute("email", (String) member.get("email"));
            model.addAttribute("birthday", (String) member.get("name"));
            model.addAttribute("customerPhone", (String) member.get("email"));
        } else if (SNSType.equals("kakao")) {
            Map<String, Map<String, String>> kakaoAccount = (Map<String, Map<String, String>>) resultBody.get("kakao_account");
            log.info("kakaoAccount={}", kakaoAccount);
            Map<String, String> member = (Map<String, String>) kakaoAccount.get("member");
            model.addAttribute("customerName", (String) member.get("name"));
            model.addAttribute("email", (String) member.get("email"));
            model.addAttribute("birthday", (String) member.get("name"));
            model.addAttribute("customerPhone", (String) member.get("email"));
        } else if (SNSType.equals("naver")) {

        }

        return "userAuth/main/signupForm";
    }

}
