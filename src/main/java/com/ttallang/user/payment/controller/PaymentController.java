package com.ttallang.user.payment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pay")
public class PaymentController {

    @GetMapping("/userPayment")
    public String test() {
        return "payment/userPayment";
    }

    @GetMapping("/user")
    public String test2() {
        return "payment/userPayment";
    }
}
