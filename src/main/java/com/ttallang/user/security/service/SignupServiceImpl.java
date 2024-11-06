package com.ttallang.user.security.service;

import com.ttallang.user.commonModel.Roles;
import com.ttallang.user.commonModel.User;
import com.ttallang.user.commomRepository.RolesRepository;
import com.ttallang.user.commomRepository.UserRepository;
import com.ttallang.user.security.config.RandomStateToken;
import com.ttallang.user.security.model.CertInfo;
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

import java.util.concurrent.ConcurrentHashMap;
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
    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------
    @Value("${payco.clientId}")
    private String paycoClientId;

    @Value("${payco.clientSecret}")
    private String paycoClientSecret;

    @Value("${kakao.clientId}")
    private String kakaoClientId;

    @Value("${kakao.clientSecret}")
    private String kakaoClientSecret;

    @Value("${naver.clientId}")
    private String naverClientId;

    @Value("${naver.clientSecret}")
    private String naverClientSecret;
    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------

    @Value("${base.redirectUri}")
    private String redirectUri;

    // 외부 서비스 로그인 창 띄우기.
    @Override
    public String getAuthorizationUrl(String SNSType) {
        String authorizationUrl = null;
        String responseType = "code";
        String clientId = null;
        String authUri = null;
        String scope = null;
        try {
            switch (SNSType) {
                case "payco" -> { // 페이코 인증.
                    RandomStateToken randomStateToken = new RandomStateToken("payco");
                    String state = randomStateToken.getRandomStateToken();
                    authUri = "https://id.payco.com/oauth2.0/authorize";
                    clientId = paycoClientId;
                    scope = "email,mobile,name,birthdayMMdd";
                    authorizationUrl = UriComponentsBuilder
                            .fromHttpUrl(authUri)
                            .queryParam("response_type", responseType)
                            .queryParam("client_id", clientId)
                            .queryParam("serviceProviderCode", "FRIENDS")
                            .queryParam("redirect_uri", redirectUri)
                            .queryParam("userLocale", "ko_KR")
                            .queryParam("scope", scope)
                            .queryParam("state", state)
                            .encode()
                            .toUriString();
                }
                case "kakao" -> { // 카카오 로그인.
                    RandomStateToken randomStateToken = new RandomStateToken("kakao");
                    String state = randomStateToken.getRandomStateToken();
                    authUri = "https://kauth.kakao.com/oauth/authorize";
                    clientId = kakaoClientId;
                    scope = "account_email,name,birthday,birthyear,phone_number";
                    authorizationUrl = UriComponentsBuilder
                            .fromHttpUrl(authUri)
                            .queryParam("response_type", responseType)
                            .queryParam("client_id", clientId)
                            .queryParam("redirect_uri", redirectUri)
                            .queryParam("scope", scope)
                            .queryParam("state", state)
                            .encode()
                            .toUriString();
                }
                case "naver" -> { // 네이버 로그인.
                    RandomStateToken randomStateToken = new RandomStateToken("naver");
                    String state = randomStateToken.getRandomStateToken();
                    authUri = "https://nid.naver.com/oauth2.0/authorize";
                    clientId = naverClientId;
                    scope = "account_email,name,birthday,birthyear,phone_number";
                    authorizationUrl = UriComponentsBuilder
                            .fromHttpUrl(authUri)
                            .queryParam("response_type", responseType)
                            .queryParam("client_id", clientId)
                            .queryParam("redirect_uri", redirectUri)
                            .queryParam("state", state)
                            .encode()
                            .toUriString();
                }
                default -> throw new RuntimeException("인증 주소가 올바르지 않습니다.");
            }
            assert authorizationUrl != null;
        } catch (RuntimeException e) {
            log.error("error={}", e.getMessage());
            return null;
        }
        return authorizationUrl;
    }

    // 유저가 외부 서비스의 로그인을 하면 액세스 토큰을 발급해줌.
    @Override
    public ResponseEntity<Map<String, String>> getAccessToken(String code, String SNSType) {
        RestTemplate restTemplate = new RestTemplate();
        String grantType = "authorization_code";
        String state = null;
        String tokenUri = null;
        String clientId = null;
        String clientSecret = null;
        HttpMethod httpMethod = null;
        HttpHeaders headers = new HttpHeaders();
        switch (SNSType) {
            case "payco" -> {
                RandomStateToken randomStateToken = new RandomStateToken("payco");
                state = randomStateToken.getRandomStateToken();
                tokenUri = "https://id.payco.com/oauth2.0/token";
                httpMethod = HttpMethod.GET;
                clientId = paycoClientId;
                clientSecret = paycoClientSecret;
            }
            case "kakao" -> {
                headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
                RandomStateToken randomStateToken = new RandomStateToken("kakao");
                state = randomStateToken.getRandomStateToken();
                tokenUri = "https://kauth.kakao.com/oauth/token";
                httpMethod = HttpMethod.POST;
                clientId = kakaoClientId;
                clientSecret = kakaoClientSecret;
            }
            case "naver" -> {
                RandomStateToken randomStateToken = new RandomStateToken("naver");
                state = randomStateToken.getRandomStateToken();
                tokenUri = "https://nid.naver.com/oauth2.0/token";
                httpMethod = HttpMethod.POST;
                clientId = naverClientId;
                clientSecret = naverClientSecret;
            }
            default -> throw new RuntimeException("토큰 요청 정보가 올바르지 않습니다.");
        }

        try {
            assert tokenUri != null;
            assert clientId != null;
            assert clientSecret != null;
            String tokenURL = UriComponentsBuilder
                    .fromHttpUrl(tokenUri)
                    .queryParam("grant_type", grantType)
                    .queryParam("client_id", clientId)
                    .queryParam("client_secret", clientSecret)
                    .queryParam("code", code)
                    .queryParam("state", state)
                    .encode()
                    .toUriString();

            assert httpMethod != null;
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                    tokenURL,
                    httpMethod,
                    entity,
                    new ParameterizedTypeReference<Map<String, String>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("액세스 토큰 얻어오기 성공...");
                try {
                    assert response.getBody() != null;
                    String accessToken = response.getBody().get("access_token");
                    if (SNSType.equals("payco")) {
                        CertInfo certInfo = new CertInfo(accessToken, clientId, clientSecret, "PACYO");
                        CertInfo.sharedCertInfoMap.put(accessToken, certInfo);
                    } else if (SNSType.equals("kakao")) {
                        CertInfo certInfo = new CertInfo(accessToken, "KAKAO");
                        CertInfo.sharedCertInfoMap.put(accessToken, certInfo);
                    } else {
                        CertInfo certInfo = new CertInfo("delete", accessToken, clientId, clientSecret, "NAVER");
                        CertInfo.sharedCertInfoMap.put(accessToken, certInfo);
                    }

                } catch (RestClientException e) {
                    log.error("응답 객체의 본문이 NULL 임: {}", e.getMessage());
                }
                return response;
            } else {
                log.error("액세스 토큰 얻어오기 실패: {}", response.getStatusCode());
                //throw new RuntimeException("액세스 토큰 얻어오기 실패...");
                return null;
            }
        } catch (RestClientException e) {
            log.error("토큰 서버 연결 실패", e);
            //throw new RuntimeException("토큰 서버 연결 실패...");
            return null;
        }
    }

    // 액세스 토큰 던져서 유저정보 가져오기.
    // 그러면서 동시에 유저와의 연결을 끊어버려야 함.
    @Override
    public ResponseEntity<Map<String, Object>> getUserInfo(String accessToken, String SNSType) {

        RestTemplate restTemplate = new RestTemplate();
        String userInfoUri = null;
        HttpHeaders headers = null;

        switch (SNSType) {
            case "payco" -> {
                headers = new HttpHeaders();
                headers.add("client_id", paycoClientId);
                headers.add("access_token", accessToken);
                userInfoUri = "https://apis-payco.krp.toastoven.net/payco/friends/find_member_v2.json";
            }
            case "kakao" -> {
                headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + accessToken);
                headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
                userInfoUri = "https://kapi.kakao.com/v2/user/me";
            }
            case "naver" -> {
                headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + accessToken);
                userInfoUri = "https://openapi.naver.com/v1/nid/me";
            }
        }

        try {
            assert headers != null;
            HttpEntity<String> entity = new HttpEntity<>(headers);
            assert userInfoUri != null;
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    userInfoUri,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("유저 정보 얻어오기 성공...");
                return response;
            } else {
                log.error("유저 정보 얻어오기 실패: {}", response.getStatusCode());
                //throw new RuntimeException("유저 정보 얻어오기 실패...");
                return null;
            }
        } catch (RestClientException e) {
            log.error("유저 정보 서버 연결 실패: ", e);
            //throw new RuntimeException("유저 정보 서버 연결 실패...");
            return null;
        }
    }

    // 회원 연동정보 해제.
    @Override
    public ResponseEntity<Map<String, Object>> unlinkUserCert(CertInfo certInfo) {

        RestTemplate restTemplate = new RestTemplate();
        String providerType = certInfo.getServiceProvider();
        String unlinkURL;
        ResponseEntity<Map<String, Object>> response = null;
        switch (providerType) {
            case "PAYCO" -> {
                unlinkURL = UriComponentsBuilder
                        .fromHttpUrl("https://id.payco.com/oauth2.0/logout")
                        .queryParam("client_id", certInfo.getClientId())
                        .queryParam("client_secret", certInfo.getClientSecret())
                        .queryParam("token", certInfo.getAccessToken())
                        .encode()
                        .toUriString();
                response = restTemplate.exchange(
                        unlinkURL,
                        HttpMethod.POST,
                        null,
                        new ParameterizedTypeReference<Map<String, Object>>() {}
                );
            }
            case "KAKAO" -> {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + certInfo.getAccessToken());
                unlinkURL = UriComponentsBuilder
                        .fromHttpUrl("https://kapi.kakao.com/v1/user/unlink")
                        .queryParam("target_id_type", certInfo.getTargetIdType())
                        .queryParam("target_id", certInfo.getTargetId())
                        .encode()
                        .toUriString();
                HttpEntity<String> entity = new HttpEntity<>(headers);
                response = restTemplate.exchange(
                        unlinkURL,
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<Map<String, Object>>() {}
                );
            }
            case "NAVER" -> {
                unlinkURL = UriComponentsBuilder
                        .fromHttpUrl("https://nid.naver.com/oauth2.0/token")
                        .queryParam("grant_type", certInfo.getGrantType())
                        .queryParam("client_id", certInfo.getClientId())
                        .queryParam("client_secret", certInfo.getClientSecret())
                        .queryParam("access_token", certInfo.getAccessToken())
                        .queryParam("service_provider", certInfo.getServiceProvider())
                        .encode()
                        .toUriString();
                response = restTemplate.exchange(
                        unlinkURL,
                        HttpMethod.POST,
                        null,
                        new ParameterizedTypeReference<Map<String, Object>>() {}
                );
            }
        }
        try {
            assert response != null;
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("회원 연동정보 해제 성공...");
                return response;
            } else {
                throw new RestClientException("연동정보 해제 실패...");
            }
        } catch (RestClientException e) {
            log.error("회원 연동정보 해제 실패: {}", response.getStatusCode());
            return null;
        }
    }
    
    // 일반 회원가입 관련.
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
        log.info("userData={}", userData);
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

        String customerPhone = userData.get("customerPhone");
        user.setCustomerPhone(customerPhone);

        String email = userData.get("email");
        user.setEmail(email);

        String birthday = userData.get("birthday");
        user.setBirthday(birthday);

        userRepository.save(user);
        // DB 기록 종료.
        System.out.println("유저 회원가입 성공: "+roles);
    }
}
