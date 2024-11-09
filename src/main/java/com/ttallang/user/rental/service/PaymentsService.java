package com.ttallang.user.rental.service;

import com.ttallang.user.commonModel.Payment;
import com.ttallang.user.commonModel.Rental;
import com.ttallang.user.commomRepository.PaymentRepository;
import com.ttallang.user.commomRepository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentsService {

    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;

    @Autowired
    public PaymentsService(PaymentRepository paymentRepository, RentalRepository rentalRepository) {
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
    }

    public Payment calculateAndSavePayment(int rentalId, int customerId) {
        Rental rental = rentalRepository.findById(rentalId)
            .orElseThrow(() -> new IllegalArgumentException("해당 대여 내역을 찾을 수 없습니다."));

        Payment payment = new Payment();
        payment.setRentalId(rental.getRentalId());
        payment.setCustomerId(customerId);
        payment.setPaymentStatus("0");

        return paymentRepository.save(payment);
    }
}
