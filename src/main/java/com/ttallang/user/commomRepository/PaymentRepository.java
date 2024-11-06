package com.ttallang.user.commomRepository;

import com.ttallang.user.commonModel.Payment;
import com.ttallang.user.payment.model.JoinPayment;
import com.ttallang.user.payment.model.JoinPortOne;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    // 결제 금액 수정
    @Query("SELECT p "
        + "FROM Payment p "
        + "WHERE p.customerId = :customerId AND p.paymentStatus = '0' ")
    Payment getByCustomerId(@Param("customerId") int customerId);

    // 결제 페이지 조회
    @Query("SELECT new com.ttallang.user.payment.model.JoinPayment(p.customerId, p.paymentId, "
        + "r.rentalBranch, r.rentalStartDate, "
        + "r.returnBranch, r.rentalEndDate, p.paymentAmount) "
        + "FROM Payment p JOIN Rental r ON p.rentalId = r.rentalId "
        + "WHERE r.customerId = :customerId AND p.paymentStatus = '0'")
    JoinPayment getByPayment(@Param("customerId") int customerId);

    // 포트원(결제 정보)
    @Query("SELECT new com.ttallang.user.payment.model.JoinPortOne("+
        "u.customerName, u.customerPhone,u.email) " +
        "FROM Payment p " +
        "JOIN User u ON p.customerId = u.customerId " +
        "WHERE u.customerId = :customerId AND p.paymentStatus = '0'")
    JoinPortOne getByJoinPortOneId(@Param("customerId") int customerId);


}
