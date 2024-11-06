package com.ttallang.user.security.controller;

import com.ttallang.user.security.model.CertInfo;
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
import java.util.Base64;

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
        model.addAttribute("customerPhone", "");
        model.addAttribute("email", (String) "");
        model.addAttribute("birthday", "");
        return "userAuth/main/signupForm";
    }

    @GetMapping("/api/oauth2/callback")
    public String callback(
            @RequestParam Map<String, String> params,
            Model model
    ) {
        // 디코딩 작업.
        String code = params.get("code");
        String state = params.get("state");
        if (code == null || state == null) {
            // 로그인 도중 취소버튼을 누르는 경우.
            return "redirect:/login/form";
        }
        System.out.println(params.toString());
        log.info("code 받기 성공={}", code);
        log.info("state 받기 성공={}", state);
        String[] stateParts = state.split(":");
        String stateTextPart = stateParts[1];
        byte[] decodedBytes = Base64.getUrlDecoder().decode(stateTextPart);
        String decodedState = new String(decodedBytes);

        String SNSType = null;
        if (decodedState.contains("payco")) {
            SNSType = "payco";
        } else if (decodedState.contains("kakao")) {
            SNSType = "kakao";
        } else if (decodedState.contains("naver")) {
            SNSType = "naver";
        } else {
            throw new RuntimeException("SNS 타입이 지정되지 않았습니다.");
        }

        Map<String, String> responseBody;
        try {
            // 인증 코드로 토큰 받기
            ResponseEntity<Map<String, String>> accessTokenResponse = signupService.getAccessToken(code, SNSType);
            log.info("토큰 응답 받기 성공={}", accessTokenResponse);
            responseBody = accessTokenResponse.getBody();
            assert responseBody != null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return "redirect:/login/form";
        }

        String accessToken = responseBody.get("access_token");
        log.info("토큰 받기 성공={}", accessToken);

        Map<String, Object> resultBody;
        try {
            // 토큰으로 사용자 정보 조회.
            ResponseEntity<Map<String, Object>> result = signupService.getUserInfo(accessToken, SNSType);
            log.info("result={}", result);
            resultBody = result.getBody();
            assert resultBody != null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return "redirect:/login/form";
        }

        CertInfo certInfo = CertInfo.sharedCertInfoMap.get(accessToken);
        switch (SNSType) {
            case "payco" -> {
                ResponseEntity<Map<String, Object>> unlinkUserCertResult = signupService.unlinkUserCert(certInfo);
                Map<String, Map<String, String>> data = (Map<String, Map<String, String>>) resultBody.get("data");
                Map<String, String> member = data.get("member");
                model.addAttribute("customerName", member.get("name"));
                model.addAttribute("customerPhone", member.get("mobile"));
                model.addAttribute("email", member.get("email"));
                model.addAttribute("birthday", member.get("birthday"));
            }
            case "kakao" -> {
                certInfo.setTargetIdType("user_id");
                Long userId = (Long) resultBody.get("id");
                certInfo.setTargetId(userId);
                ResponseEntity<Map<String, Object>> unlinkUserCertResult = signupService.unlinkUserCert(certInfo);
                Map<String, String> kakaoAccount = (Map<String, String>) resultBody.get("kakao_account");
                model.addAttribute("customerName", kakaoAccount.get("name"));
                String phoneNumber = kakaoAccount.get("phone_number");
                String replacedPhoneNumber1 = phoneNumber.replace("+82 ", "0");
                String replacedPhoneNumber2 = replacedPhoneNumber1.replace("-", "");
                model.addAttribute("customerPhone", replacedPhoneNumber2);
                model.addAttribute("email", kakaoAccount.get("email"));
                model.addAttribute("birthday", kakaoAccount.get("birthyear") + kakaoAccount.get("birthday"));
            }
            case "naver" -> {
                ResponseEntity<Map<String, Object>> unlinkUserCertResult = signupService.unlinkUserCert(certInfo);
                Map<String, String> response = (Map<String, String>) resultBody.get("response");
                model.addAttribute("customerName", response.get("name"));
                String mobile = response.get("mobile");
                String replacedMobile = mobile.replace("-", "");
                model.addAttribute("customerPhone", replacedMobile);
                model.addAttribute("email", response.get("email"));
                String birthday = response.get("birthday");
                String replacedBirthday = birthday.replace("-", "");
                model.addAttribute("birthday", response.get("birthyear") + replacedBirthday);
            }
            default -> throw new RuntimeException("SNS 타입이 지정되지 않았습니다.");
        }
        return "userAuth/main/signupForm";
    }

}
