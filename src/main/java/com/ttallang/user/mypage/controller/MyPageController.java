package com.ttallang.user.mypage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/myPage")
public class MyPageController {

    @GetMapping("/userModify")
    public String userModify() {
        return "myPage/userModify";
    }

    @GetMapping("/userRental")
    public String userRental() {
        return "myPage/userRental";
    }

    @GetMapping("/userFaultReport")
    public String userFaultReport() {
        return "myPage/userFaultReport";
    }
}
