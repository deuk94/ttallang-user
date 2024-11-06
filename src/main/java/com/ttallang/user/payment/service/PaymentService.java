package com.ttallang.user.payment.service;

import com.siot.IamportRestClient.IamportClient;
import com.ttallang.user.commonModel.Payment;
import com.ttallang.user.commonModel.Rental;
import com.ttallang.user.commomRepository.RentalRepository;
import com.ttallang.user.payment.model.JoinPayment;
import com.ttallang.user.payment.model.JoinPortOne;
import com.ttallang.user.commomRepository.PaymentRepository;
import java.io.Console;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;

    public PaymentService(PaymentRepository paymentRepository, RentalRepository rentalRepository) {
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
    }

    // 결제 테이블 조회
    public Payment getPayment(int customerId) {
        return paymentRepository.getByCustomerId(customerId);
    }

    // 결제 금액 수정
    public Payment updatePaymentAmount(int rentalId, int paymentId) {
        Rental rental = rentalRepository.findById(rentalId).orElse(null);
        long minutes = Duration.between(rental.getRentalStartDate(), rental.getRentalEndDate()).toMinutes();
        int paymentAmount = 500 + (int) (minutes * 150);

        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        payment.setPaymentAmount(paymentAmount);
        return paymentRepository.save(payment);
    }

    // 결제 페이지 조회
    public JoinPayment getByPayment(int customerId) {
        return paymentRepository.getByPayment(customerId);
    }

    // 결제 버튼 후 db 정보 수정
    public Payment updatePayment(int customerId) {
        Payment payment = getPayment(customerId);

        LocalDateTime now = LocalDateTime.now();
        payment.setPaymentDate(now);
        payment.setPaymentStatus("1");
        return paymentRepository.save(payment);
    }

    // 결제 정보 조회
    public JoinPortOne getByPaymentInfo(int customerId) {
        return paymentRepository.getByJoinPortOneId(customerId);
    }

    // 결제 금액 검증
    public boolean validatePaymentAmount(int paymentId, int amount) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        return payment != null && payment.getPaymentAmount() == amount;
    }

}