package com.ttallang.user.security.controller;

import com.ttallang.user.security.service.FindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
public class FindController {

    private final FindService findService;

    public FindController(FindService findService) {
        this.findService = findService;
    }

    @GetMapping("/find/select")
    public String find() {
        return "userAuth/main/findSelect";
    }

    @GetMapping("/find/username")
    public String inputCustomerPhone() {
        return "userAuth/main/inputCustomerPhone";
    }

    @GetMapping("/find/username/result")
    public String result() {
        return "userAuth/main/inputCustomerPhone";
    }
}
