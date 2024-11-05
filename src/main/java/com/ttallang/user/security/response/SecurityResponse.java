package com.ttallang.user.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 응답 객체를 Map 으로 하지 않고 이렇게 따로 만든 이유는 매번 Map으로 만들면 응답 구조에 대해서 까먹고 일관성이 없어질까봐 고정하기 위해 만들었음...
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityResponse {
    private int code;
    private String status;
    private String role;
    private String message;
}
