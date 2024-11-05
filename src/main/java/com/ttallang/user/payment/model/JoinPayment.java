package com.ttallang.user.payment.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinPayment {

    private int customerId;
    private int paymentId;
    private String rentalBranch;
    private LocalDateTime rentalStartDate;
    private String returnBranch;
    private LocalDateTime rentalEndDate;
    private Integer paymentAmount;
}
