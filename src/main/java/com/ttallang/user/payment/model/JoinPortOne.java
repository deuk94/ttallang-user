package com.ttallang.user.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinPortOne {

    private int customerId;
    private int paymentId;
    private String customerName;
    private String customerPhone;
    private int paymentAmount;
    private String email;
}
