package com.ttallang.user.userAuth.security.service;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaycoOAuth2UserService extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processPaycoOAuth2User(userRequest, oauth2User);
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException("OAuth2 처리 중 오류가 발생했습니다.", e);
        }
    }

    private OAuth2User processPaycoOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        // 페이코 응답 데이터 처리
        Map<String, Object> attributes = oauth2User.getAttributes();

        System.out.println(attributes.toString());

        return oauth2User;
    }
}
