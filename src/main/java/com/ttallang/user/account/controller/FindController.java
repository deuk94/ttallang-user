package com.ttallang.user.account.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class FindController {

    @GetMapping("/find/select")
    public String find() {
        return "account/find/select";
    }

    @GetMapping("/find/username/input")
    public String inputCustomerPhone() {
        return "account/find/id";
    }
}
