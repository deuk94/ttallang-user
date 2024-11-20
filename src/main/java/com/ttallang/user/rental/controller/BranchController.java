package com.ttallang.user.rental.controller;

import com.ttallang.user.security.config.auth.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class BranchController {

    @GetMapping("/main")
    public String userMainPage(Model model) {
        try {
            PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("username", pds.getUsername());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.info("메인 페이지 접속중 에러가 발생하였습니다.");
            // 이 경우 pds 찍었을 때 유저 객체가 anonymous로 나온다면 크롬 보안정책 (samesite, https) 관련 문제일 가능성이 높음.
            // 엣지에서 하면 잘 될것임.
            // 크롬에서는 https 를 적용할 수 밖에 없는 것 같음.
            return "redirect:/login/form";
        }
        return "main/mainPage";
    }
}