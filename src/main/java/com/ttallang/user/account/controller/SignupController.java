package com.ttallang.user.account.controller;

import com.ttallang.user.account.model.CertInfo;
import com.ttallang.user.account.service.SignupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Controller
public class SignupController { // 리턴 타입이 JSP 페이지인 컨트롤러.

    private final SignupService signupService;
    private final Map<String, CertInfo> sharedCertInfoMap;

    public SignupController(SignupService signupService, Map<String, CertInfo> sharedCertInfoMap) {
        this.signupService = signupService;
        this.sharedCertInfoMap = sharedCertInfoMap;
    }

    // SNS 계정연동 및 일반가입 선택하는 페이지.
    @GetMapping("/signup/select")
    public String signupSelect() {
        return "account/signup/select";
    }

    @GetMapping("/signup/form")
    public String signupForm(Model model) {
        model.addAttribute("customerName", "");
        model.addAttribute("customerPhone", "");
        model.addAttribute("email", (String) "");
        model.addAttribute("birthday", "");
        return "account/signup/form";
    }

    @GetMapping("/oauth2/callback") // 여기서 getAuthorizationUrl 이후의 나머지 작업들을 모두 포함함 (processSNSCert 는 SignupServiceImpl 의 라인 363부터 시작.)
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
        CertInfo certInfo = sharedCertInfoMap.get(accessToken);
        String targetPage = signupService.fillOutSignupForm(responseBody, SNSType, certInfo, model);
        sharedCertInfoMap.remove(accessToken); // 임시 인증객체 제거.
        return targetPage;
    }
}
