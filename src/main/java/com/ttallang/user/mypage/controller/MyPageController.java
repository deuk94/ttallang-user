package com.ttallang.user.mypage.controller;

import com.ttallang.user.security.config.auth.PrincipalDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/myPage")
public class MyPageController {

    @GetMapping("/userModify")
    public String userModify(Model model) {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("customerId", pds.getCustomerID());
        model.addAttribute("username", pds.getUsername());
        return "myPage/userModify";
    }

    @GetMapping("/userRental")
    public String userRental(Model model) {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("customerId", pds.getCustomerID());
        model.addAttribute("username", pds.getUsername());
        return "myPage/userRental";
    }

    @GetMapping("/userFaultReport")
    public String userFaultReport(Model model) {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("customerId", pds.getCustomerID());
        model.addAttribute("username", pds.getUsername());
        return "myPage/userFaultReport";
    }
}
