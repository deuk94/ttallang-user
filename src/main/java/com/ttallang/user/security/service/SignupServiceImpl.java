package com.ttallang.user.security.service;

import com.ttallang.user.commonModel.Roles;
import com.ttallang.user.commonModel.User;
import com.ttallang.user.commomRepository.RolesRepository;
import com.ttallang.user.commomRepository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Service
public class SignupServiceImpl implements SignupService {

    // 의존성 주입.
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // 암호화해주는놈.
    private final RolesRepository rolesRepository;
    private final UserRepository userRepository;

    public SignupServiceImpl(
            BCryptPasswordEncoder bCryptPasswordEncoder,
            RolesRepository rolesRepository,
            UserRepository userRepository
    ) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.rolesRepository = rolesRepository;
        this.userRepository = userRepository;
    }

    // 민감 정보 변수화.
    // ------------------------------------------------------------------------------------
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
    // ------------------------------------------------------------------------------------

    // 페이코 로그인 창 띄우기.
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

    // 액세스 토큰 던져서 유저정보 가져오기.
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

    // 일반 회원가입 관련
    // ---------------------------------------------------------------------------
    // 아이디 존재 여부 확인.
    @Override
    public boolean isExistingCustomer(String userName) {
        Roles roles = rolesRepository.findByUserName(userName);
        return roles != null;
    }

    // 일반 회원 가입.
    @Override
    public void signupCustomer(Map<String, String> userData) {
        // 유저 권한 테이블 기록.
        Roles roles = new Roles();

        String userName = userData.get("userName");
        roles.setUserName(userName);

        roles.setUserRole("ROLE_USER");

        String rawPassword = userData.get("userPassword");
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        roles.setUserPassword(encPassword);

        roles.setUserStatus("1");

        rolesRepository.save(roles);
        // 유저 상세 데이터 기록.
        User user = new User();
        int userId = roles.getUserId();
        user.setUserId(userId);

        String customerName = userData.get("customerName");
        user.setCustomerName(customerName);

        String email = userData.get("email");
        user.setEmail(email);

        String customerPhone = "01012345678"; // 임시
        user.setCustomerPhone(customerPhone);

        String birthday = "20000101"; // 임시
        user.setBirthday(birthday);

        userRepository.save(user);
        // DB 기록 종료.
        System.out.println("유저 회원가입 성공: "+roles);
    }

    // 관리자 가입.
    @Override
    public void signupAdmin(Map<String, String> userData) {
        Roles roles = new Roles();

        String userName = userData.get("userName");
        roles.setUserName(userName);

        roles.setUserRole("ROLE_ADMIN");

        String rawPassword = userData.get("userPassword");
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        roles.setUserPassword(encPassword);

        roles.setUserStatus("1");

        rolesRepository.save(roles);
    }
}
