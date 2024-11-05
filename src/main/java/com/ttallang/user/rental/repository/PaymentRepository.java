package com.ttallang.user.rental.repository;

import com.ttallang.user.commonModel.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    // customerId에 따른 최신 결제 내역 조회
    @Query("SELECT p FROM Payment p WHERE p.customerId = :customerId ORDER BY p.id DESC")
    Optional<Payment> findLatestPaymentByCustomerId(@Param("customerId") int customerId);
}
