package com.ttallang.user.commomRepository;

import com.ttallang.user.commonModel.User;
import com.ttallang.user.mypage.model.JoinUser;
import com.ttallang.user.security.model.PaymentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT new com.ttallang.user.mypage.model.JoinUser(" +
        "r.userName, u.customerName, r.userPassword, " +
        "u.customerPhone, u.birthday, u.email) " +
        "FROM User u JOIN Roles r ON u.userId = r.userId " +
        "WHERE u.customerId = :customerId AND r.userStatus = '1' ")
    JoinUser findByUser(@Param("customerId") int customerId);

    User findByUserId(int userId);

    @Query("select new com.ttallang.user.security.model.PaymentUser(u.customerId, p.paymentStatus) " +
            "from User as u " +
            "join Payment as p " +
            "on u.customerId = p.customerId " +
            "where p.customerId = :customerId AND p.paymentStatus = '0' ")
    PaymentUser findNoPaymentUser(@Param("customerId") int customerId);
}

