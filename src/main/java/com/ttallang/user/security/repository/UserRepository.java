package com.ttallang.user.security.repository;

import com.ttallang.user.commonModel.User;
import com.ttallang.user.security.model.PaymentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserId(int userId);

    @Query("select new com.ttallang.user.security.model.JoinUser(u.customerId, p.paymentStatus) " +
            "from User as u " +
            "join Payment as p " +
            "on u.customerId = p.customerId " +
            "where p.customerId = :customerId AND p.paymentStatus = '0' ")
    PaymentUser findNoPaymentUser(@Param("customerId") int customerId);
}
