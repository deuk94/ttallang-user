package com.ttallang.user.security.config;

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

    // SNS 구분 및 주소 탈취 방지를 위한 state 발생기.
    private String generate(String provider) {
        SecureRandom secureRandom = new SecureRandom();
        BigInteger random = new BigInteger(130, secureRandom);
        String encodedProvider = Base64.getEncoder().encodeToString(provider.getBytes());
        return random + ":" + encodedProvider;
    }
}
