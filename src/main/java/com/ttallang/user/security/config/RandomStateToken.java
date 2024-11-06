package com.ttallang.user.security.config;

import java.util.Base64;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RandomStateToken {

    private final String randomStateToken;

    public RandomStateToken(String provider) {
        this.randomStateToken = this.generate(provider);
    }

    private String generate(String provider) {
        SecureRandom secureRandom = new SecureRandom();
        BigInteger random = new BigInteger(130, secureRandom);
        String encodedProvider = Base64.getEncoder().encodeToString(provider.getBytes());
        return random + ":" + encodedProvider;
    }

    public String getRandomStateToken() {
        return randomStateToken;
    }
}
