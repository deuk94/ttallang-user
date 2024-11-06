package com.ttallang.user.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinPortOne {

    private String customerName;
    private String customerPhone;
    private String email;
}
