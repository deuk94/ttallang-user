package com.ttallang.user.security.config.token;

import lombok.Getter;

import java.util.Base64;

import java.math.BigInteger;
import java.security.SecureRandom;

@Getter
public class RandomStateToken {

    private final String randomStateToken;

    public RandomStateToken(String provider) {
        this.randomStateToken = this.generate(provider);
    }

    // 주소 탈취 방지를 위한 state 발생기.
    // SNS 계정 연동에 사용될 시 어떤 SNS 공급자인지 구분하는 역할도 한다.
    private String generate(String target) {
        SecureRandom secureRandom = new SecureRandom();
        BigInteger random = new BigInteger(130, secureRandom);
        String encodedTarget = Base64.getEncoder().encodeToString(target.getBytes());
        return random + ":" + encodedTarget;
    }
}
