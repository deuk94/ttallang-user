package com.ttallang.user.test.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
public class ApiController {
    @GetMapping(value = "/pass/test")
    public String getPassTestPage(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
//        CPClient niceCheck = new CPClient();
//
//        String sSiteCode = "직접작성! 사이트 코드";            // NICE로부터 부여받은 사이트 코드
//        String sSitePassword = "직접작성! 사이트 패스워드";        // NICE로부터 부여받은 사이트 패스워드
//
//        String sRequestNumber = niceCheck.getRequestNO(sSiteCode);        // 요청 번호, 이는 성공/실패후에 같은 값으로 되돌려주게 되므로
//
//        String sAuthType = "";        // 없으면 기본 선택화면, M: 핸드폰, C: 신용카드, X: 공인인증서
//
//        String popgubun = "N";        //Y : 취소버튼 있음 / N : 취소버튼 없음
//        String customize = "";        // 없으면 기본 웹페이지 / Mobile : 모바일페이지
//        String sGender = "";            // 없으면 기본 선택 값, 0 : 여자, 1 : 남자
//
//        String sReturnUrl = "http://localhost:8080/pass/success"; // 성공시 이동될 URL
//        String sErrorUrl = "http://localhost:8080/pass/fail"; // 실패시 이동될 URL
//
//        // 입력될 plain 데이타를 만든다.
//        String sPlainData = "7:REQ_SEQ" + sRequestNumber.getBytes().length + ":" + sRequestNumber +
//                "8:SITECODE" + sSiteCode.getBytes().length + ":" + sSiteCode +
//                "9:AUTH_TYPE" + sAuthType.getBytes().length + ":" + sAuthType +
//                "7:RTN_URL" + sReturnUrl.getBytes().length + ":" + sReturnUrl +
//                "7:ERR_URL" + sErrorUrl.getBytes().length + ":" + sErrorUrl +
//                "11:POPUP_GUBUN" + popgubun.getBytes().length + ":" + popgubun +
//                "9:CUSTOMIZE" + customize.getBytes().length + ":" + customize +
//                "6:GENDER" + sGender.getBytes().length + ":" + sGender;
//
//        String sMessage = "";
//        String sEncData = "";
//
//        int iReturn = niceCheck.fnEncode(sSiteCode, sSitePassword, sPlainData);
//        if (iReturn == 0) {
//            sEncData = niceCheck.getCipherData();
//        } else if (iReturn == -1) {
//            sMessage = "암호화 시스템 에러입니다.";
//        } else if (iReturn == -2) {
//            sMessage = "암호화 처리오류입니다.";
//        } else if (iReturn == -3) {
//            sMessage = "암호화 데이터 오류입니다.";
//        } else if (iReturn == -9) {
//            sMessage = "입력 데이터 오류입니다.";
//        } else {
//            sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
//        }
//
//        request.getSession().setAttribute("REQ_SEQ", sRequestNumber);
//
//        modelMap.addAttribute("sMessage", sMessage);
//        modelMap.addAttribute("sEncData", sEncData);
//
//        return "pass_test";
        return "main";
    }
}
