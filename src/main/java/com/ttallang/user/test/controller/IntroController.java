package com.ttallang.user.test.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"", "/intro"})
public class IntroController {
    @GetMapping()
    public String introPage(HttpServletRequest request, HttpServletResponse response) {
        return "intro";
    }
}
