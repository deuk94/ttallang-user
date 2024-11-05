package com.ttallang.user.mypage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinUser {

    private String userName;
    private String customerName;
    private String userPassword;
    private String customerPhone;
    private String birthday;
    private String email;
}
