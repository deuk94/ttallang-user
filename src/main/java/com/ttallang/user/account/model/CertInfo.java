package com.ttallang.user.account.model;

import lombok.Getter;
import lombok.Setter;

// 유저 인증 정보를 임시로 관리하기 위한 클래스.
@Getter
public class CertInfo {
    private final String grantType;
    private final String accessToken;
    private final String clientId;
    private final String clientSecret;
    private final String serviceProvider;
    @Setter
    private String targetIdType;
    @Setter
    private Long targetId;

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
