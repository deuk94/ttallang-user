package com.ttallang.user.userAuth.signup.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Service
public class SignupServiceImpl implements SignupService {

    @Value("${spring.security.oauth2.client.registration.payco.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.payco.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.payco.redirect-uri}")
    private String redirectURI;

    @Value("${spring.security.oauth2.client.provider.payco.authorization-uri}")
    private String authUri;

    @Value("${spring.security.oauth2.client.provider.payco.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.registration.payco.authorization-grant-type}")
    private String grantType;

    @Value("${spring.security.oauth2.client.provider.payco.user-info-uri}")
    private String userInfoUri;

    @Override
    public String getAuthorizationUrl() {
        return UriComponentsBuilder
                .fromHttpUrl(authUri)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("serviceProviderCode", "FRIENDS")
                .queryParam("redirect_uri", redirectURI)
                .queryParam("userLocale", "ko_KR")
                .encode()
                .toUriString();
    }

    // 유저가 페이코 로그인을 하면 액세스 토큰을 발급해줌.
    public ResponseEntity<Map<String, String>> getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String tokenURL = UriComponentsBuilder
                .fromHttpUrl(tokenUri)
                .queryParam("grant_type", grantType)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .encode()
                .toUriString();

        try {
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                    tokenURL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, String>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("페이코 액세스 토큰 얻어오기 성공...");
                return response;
            } else {
                log.error("페이코 액세스 토큰 얻어오기 실패: {}", response.getStatusCode());
                throw new RuntimeException("페이코 액세스 토큰 얻어오기 실패...");
            }
        } catch (RestClientException e) {
            log.error("페이코 서버 연결 실패", e);
            throw new RuntimeException("페이코 서버 연결 실패...");
        }
    }

    public ResponseEntity<Map<String, Object>> getUserInfo(String accessToken) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("client_id", clientId);
        headers.add("access_token", accessToken);

        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    userInfoUri,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("페이코 유저 정보 얻어오기 성공...");
                return response;
            } else {
                log.error("페이코 유저 정보 얻어오기 실패: {}", response.getStatusCode());
                throw new RuntimeException("페이코 유저 정보 얻어오기 실패...");
            }
        } catch (RestClientException e) {
            log.error("페이코 서버 연결 실패", e);
            throw new RuntimeException("페이코 서버 연결 실패...");
        }
    }
}
