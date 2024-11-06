package com.ttallang.user.security.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CertInfo {
    @Getter
    private final String grantType;
    @Getter
    private final String accessToken;
    @Getter
    private final String clientId;
    @Getter
    private final String clientSecret;
    @Getter
    private final String serviceProvider;
    @Getter @Setter
    private String targetIdType;
    @Getter @Setter
    private Long targetId;

    public static final Map<String, CertInfo> sharedCertInfoMap = new ConcurrentHashMap<>(); // 유저 인증 정보를 임시로 저장하기 위한 DB처럼 쓰려고 만듦.

    public CertInfo( // 페이코 버전.
        String accessToken,
        String clientId,
        String clientSecret,
        String serviceProvider
    ) {
        this.grantType = null;
        this.accessToken = accessToken;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.serviceProvider = serviceProvider;
        this.targetIdType = null;
        this.targetId = null;
    }

    public CertInfo( // 카카오 버전.
        String accessToken,
        String serviceProvider
    ) {
        this.grantType = null;
        this.accessToken = accessToken;
        this.clientId = null;
        this.clientSecret = null;
        this.serviceProvider = serviceProvider;
        this.targetIdType = null;
        this.targetId = null;
    }

    public CertInfo( // 네이버 버전.
        String grantType,
        String accessToken,
        String clientId,
        String clientSecret,
        String serviceProvider
    ) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.serviceProvider = serviceProvider;
        this.targetIdType = null;
        this.targetId = null;
    }
}
