package com.ttallang.user.payment.controller;

import com.ttallang.user.commonModel.Payment;
import com.ttallang.user.payment.model.JoinPayment;
import com.ttallang.user.payment.model.JoinPortOne;
import com.ttallang.user.payment.service.PaymentService;
import com.ttallang.user.security.config.auth.PrincipalDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay")
public class PaymentRestController {

    private final PaymentService paymentService;

    public PaymentRestController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 결제 금액 수정
    @PatchMapping("/updateAmount")
    public Payment updatePaymentAmount() {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        Payment payment = paymentService.getPayment(loginId);
        int rentalId = payment.getRentalId();
        int paymentId = payment.getPaymentId();
        return paymentService.updatePaymentAmount(rentalId, paymentId);
    }

    // 결제 페이지 보여주기
    @GetMapping("/payment")
    public JoinPayment getPayment() {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        return paymentService.getByPayment(loginId);
    }

    // 결제 버튼 누른 후 내역 수정
    @PatchMapping("/payment")
    public Payment updatePayment() {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        Payment payment = paymentService.getPayment(loginId);
        int paymentId = payment.getPaymentId();
        int paymentAmount = payment.getPaymentAmount();
        System.out.println(payment.getPaymentId());

        // 결제 금액 검증
        if (!paymentService.validatePaymentAmount(paymentId, paymentAmount)) {
            throw new RuntimeException("결제 금액이 일치하지 않습니다."); // 예외 처리
        }

        return paymentService.updatePayment(loginId);
    }

    // 결제 정보 조회
    @GetMapping("/paymentInfo")
    public JoinPortOne paymentInfo() {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        return paymentService.getByPaymentInfo(loginId);
    }
}
