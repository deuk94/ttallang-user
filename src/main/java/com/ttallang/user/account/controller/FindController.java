package com.ttallang.user.account.controller;

import com.ttallang.user.account.service.FindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/find")
public class FindController {

    private final FindService findService;

    public FindController(FindService findService) {
        this.findService = findService;
    }

    @GetMapping("/select")
    public String find() {
        return "account/find/select";
    }

    @GetMapping("/username/input")
    public String inputCustomerPhone() {
        return "account/find/id";
    }

    @GetMapping("/password/input")
    public String inputUsernameCustomerPhone() {
        return "account/find/pw";
    }

    @GetMapping("/changePassword")
    public String renderPasswordChangePage(@RequestParam String state, Model model) {
        return findService.renderPasswordChangePage(state, model);
    }
}
