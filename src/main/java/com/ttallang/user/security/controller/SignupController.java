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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

@Slf4j
@Controller
public class SignupController { // 리턴 타입이 JSP 페이지인 컨트롤러.

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

    @GetMapping("/oauth2/callback")
    public String callback(@RequestParam Map<String, String> params, Model model) {
        Map<String, Object> responseBody = signupService.processSNSCert(params);
        if (responseBody.get("cancel") != null) {
            String encodedMessage = URLEncoder.encode("SNS 인증이 취소되었습니다.", StandardCharsets.UTF_8);
            return "redirect:/login/form?cancel="+encodedMessage;
        }
        if (responseBody.get("error") != null) {
            String encodedMessage = URLEncoder.encode("에러가 발생하였습니다.", StandardCharsets.UTF_8);
            return "redirect:/login/form?error="+encodedMessage;
        }
        String SNSType = (String) responseBody.get("SNSType");
        String accessToken = (String) responseBody.get("accessToken");
        CertInfo certInfo = CertInfo.sharedCertInfoMap.get(accessToken);
        switch (SNSType) {
            case "payco" -> {
                // 페이코는 연동 해제 주소가 따로 없음.
                // signupService.unlinkUserCert(certInfo);
                Map<String, Map<String, String>> data = (Map<String, Map<String, String>>) responseBody.get("data");
                Map<String, String> member = data.get("member");
                // 유저 중복 검사.
                String customerPhone = member.get("mobile");
                String email = member.get("email");
                if (signupService.isExistingCustomer(email, customerPhone)) { // 중복 유저가 존재하는 경우.
                    String encodedMessage = URLEncoder.encode("이미 해당 정보로 가입한 유저가 있습니다.", StandardCharsets.UTF_8);
                    return "redirect:/login/form?error="+encodedMessage;
                };
                model.addAttribute("customerName", member.get("name"));
                model.addAttribute("customerPhone", customerPhone);
                model.addAttribute("email", email);
                model.addAttribute("birthday", member.get("birthday"));
            }
            case "kakao" -> {
                certInfo.setTargetIdType("user_id");
                Long userId = (Long) responseBody.get("id");
                certInfo.setTargetId(userId);
                signupService.unlinkUserCert(certInfo);
                Map<String, String> kakaoAccount = (Map<String, String>) responseBody.get("kakao_account");
                // 유저 중복 검사.
                String phoneNumber = kakaoAccount.get("phone_number");
                String replacedPhoneNumber1 = phoneNumber.replace("+82 ", "0");
                String replacedPhoneNumber2 = replacedPhoneNumber1.replace("-", "");
                String email = kakaoAccount.get("email");
                if (signupService.isExistingCustomer(email, replacedPhoneNumber2)) { // 중복 유저가 존재하는 경우.
                    String encodedMessage = URLEncoder.encode("이미 해당 정보로 가입한 유저가 있습니다.", StandardCharsets.UTF_8);
                    return "redirect:/login/form?error="+encodedMessage;
                };
                model.addAttribute("customerName", kakaoAccount.get("name"));
                model.addAttribute("customerPhone", replacedPhoneNumber2);
                model.addAttribute("email", email);
                model.addAttribute("birthday", kakaoAccount.get("birthyear") + kakaoAccount.get("birthday"));
            }
            case "naver" -> {
                signupService.unlinkUserCert(certInfo);
                Map<String, String> response = (Map<String, String>) responseBody.get("response");
                // 유저 중복 검사.
                String mobile = response.get("mobile");
                String replacedMobile = mobile.replace("-", "");
                String email = response.get("email");
                if (signupService.isExistingCustomer(email, replacedMobile)) { // 중복 유저가 존재하는 경우.
                    String encodedMessage = URLEncoder.encode("이미 해당 정보로 가입한 유저가 있습니다.", StandardCharsets.UTF_8);
                    return "redirect:/login/form?error="+encodedMessage;
                };
                model.addAttribute("customerName", response.get("name"));
                model.addAttribute("customerPhone", replacedMobile);
                model.addAttribute("email", email);
                String birthday = response.get("birthday");
                String replacedBirthday = birthday.replace("-", "");
                model.addAttribute("birthday", response.get("birthyear") + replacedBirthday);
            }
            default -> {
                String encodedMessage = URLEncoder.encode("SNS 타입이 지정되지 않았습니다.", StandardCharsets.UTF_8);
                return "redirect:/login/form?error="+encodedMessage;
            }
        }
        CertInfo.sharedCertInfoMap.remove(accessToken); // 임시 인증객체 제거.
        return "userAuth/main/signupForm";
    }
}
