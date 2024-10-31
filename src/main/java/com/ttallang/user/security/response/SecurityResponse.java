package com.ttallang.user.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityResponse {
    private int code;
    private String status;
    private String role;
    private String message;
}
