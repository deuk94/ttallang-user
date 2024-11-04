package com.ttallang.user.rental.repository;

import com.ttallang.user.commonModel.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
