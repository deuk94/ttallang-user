package com.ttallang.user.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotPaymentUser {

    private int customerId;
    private String paymentStatus;
}
