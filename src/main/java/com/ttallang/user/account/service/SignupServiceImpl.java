package com.ttallang.user.account.service;

import com.ttallang.user.commonModel.Roles;
import com.ttallang.user.commonModel.User;
import com.ttallang.user.commomRepository.RolesRepository;
import com.ttallang.user.commomRepository.UserRepository;
import com.ttallang.user.security.config.token.RandomStateToken;
import com.ttallang.user.account.model.CertInfo;
import com.ttallang.user.account.model.AccountResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
public class SignupServiceImpl implements SignupService {

    /*
    이 클래스에는 private 메서드들이 있는데 그것들은 원래 컨트롤러에서 구현할려다가 컨트롤러 길이가 길어져서,
    여기로 따로 뺀 로직들임. 서비스 클래스 내부에서만 사용함.
    */

    // 의존성 주입.
    private final Map<String, CertInfo> sharedCertInfoMap;
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // 암호화해주는놈.
    private final RolesRepository rolesRepository;
    private final UserRepository userRepository;

    public SignupServiceImpl(
            BCryptPasswordEncoder bCryptPasswordEncoder,
            RolesRepository rolesRepository,
            UserRepository userRepository,
            Map<String, CertInfo> sharedCertInfoMap
    ) {
        this.sharedCertInfoMap = sharedCertInfoMap;
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

    @Value("${google.clientId}")
    private String googleClientId;

    @Value("${google.clientSecret}")
    private String googleClientSecret;
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
                case "google" -> { // 구글 인증.
                    // 문서 찾기 힘들었다...https://developers.google.com/identity/protocols/oauth2/web-server?hl=ko#httprest_3
                    RandomStateToken randomStateToken = new RandomStateToken("google");
                    String state = randomStateToken.getRandomStateToken();
                    authUri = "https://accounts.google.com/o/oauth2/v2/auth";
                    clientId = googleClientId;
                    scope = "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";
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

    // 인가 코드를 받아서 액세스 토큰을 받기 위해 가공함.
    private Map<String, String> getAuthorizationCodeAndSNSType(Map<String, String> params) {
        Map<String, String> authorizationCodeMap = new HashMap<>();
        String code = params.get("code");
        String state = params.get("state");
        if (code == null || state == null) {
            // 로그인 도중 취소버튼을 누르는 경우.
            authorizationCodeMap.put("cancel", "true");
            return authorizationCodeMap;
        }
        log.info("code={} | state={}", code, state); // 인가코드 받기 성공.
        String[] stateParts = state.split(":");
        String stateTextPart = stateParts[1];
        byte[] decodedBytes = Base64.getUrlDecoder().decode(stateTextPart);
        String decodedState = new String(decodedBytes);

        String SNSType = null;
        if (decodedState.contains("payco")) {
            SNSType = "payco";
        } else if (decodedState.contains("kakao")) {
            SNSType = "kakao";
        } else if (decodedState.contains("naver")) {
            SNSType = "naver";
        } else if (decodedState.contains("google")) {
            SNSType = "google";
        } else {
            throw new RuntimeException("SNS 타입이 지정되지 않았습니다.");
        }
        authorizationCodeMap.put("code", code);
        authorizationCodeMap.put("SNSType", SNSType);
        authorizationCodeMap.put("cancel", "false");
        return authorizationCodeMap;
    }

    // 유저가 외부 서비스의 로그인을 하면 액세스 토큰을 발급해줌.
    private ResponseEntity<Map<String, Object>> getAccessToken(Map<String, String> authorizationCodeMap) {
        RestTemplate restTemplate = new RestTemplate();
        String grantType = "authorization_code";
        String tokenURL;
        String state = null;
        String tokenUri = null;
        String clientId = null;
        String clientSecret = null;
        HttpMethod httpMethod = null;
        HttpHeaders headers = new HttpHeaders();

        String code = authorizationCodeMap.get("code");
        String SNSType = authorizationCodeMap.get("SNSType");
        log.info("authorizationCodeMap={}", authorizationCodeMap);

        try {
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
                case "google" -> {
                    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
                    RandomStateToken randomStateToken = new RandomStateToken("google");
                    state = randomStateToken.getRandomStateToken();
                    tokenUri = "https://oauth2.googleapis.com/token";
                    httpMethod = HttpMethod.POST;
                    clientId = googleClientId;
                    clientSecret = googleClientSecret;
                }
                default -> throw new RuntimeException("토큰 요청 정보가 올바르지 않습니다.");
            }

            if (SNSType.equals("google")) {
                tokenURL = UriComponentsBuilder
                        .fromHttpUrl(tokenUri)
                        .queryParam("grant_type", grantType)
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("code", code)
                        .queryParam("state", state)
                        .encode()
                        .toUriString();
            } else {
                tokenURL = UriComponentsBuilder
                        .fromHttpUrl(tokenUri)
                        .queryParam("grant_type", grantType)
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("code", code)
                        .queryParam("state", state)
                        .encode()
                        .toUriString();
            }

            assert httpMethod != null;
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    tokenURL,
                    httpMethod,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                try {
                    Map<String, Object> responseBody = response.getBody();
                    assert responseBody != null;
                    log.info("응답 받기 성공... | responseBody={}", responseBody);
                    String accessToken = (String) responseBody.get("access_token");
                    log.info("액세스 토큰 얻어오기 성공... | accessToken={}", accessToken);
                    if (SNSType.equals("payco")) {
                        CertInfo certInfo = new CertInfo(accessToken, clientId, clientSecret, "PACYO");
                        sharedCertInfoMap.put(accessToken, certInfo);
                    } else if (SNSType.equals("kakao")) {
                        CertInfo certInfo = new CertInfo(accessToken, "KAKAO");
                        sharedCertInfoMap.put(accessToken, certInfo);
                    } else if (SNSType.equals("google")) {
                        CertInfo certInfo = new CertInfo(accessToken, clientSecret, "GOOGLE");
                        sharedCertInfoMap.put(accessToken, certInfo);
                    } else {
                        CertInfo certInfo = new CertInfo("delete", accessToken, clientId, clientSecret, "NAVER");
                        sharedCertInfoMap.put(accessToken, certInfo);
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
            log.error("error={}", e.getMessage());
            log.error("토큰 서버 연결 실패", e);
            //throw new RuntimeException("토큰 서버 연결 실패...");
            return null;
        }
    }

    // 액세스 토큰 던져서 유저정보 가져오기.
    // 그러면서 동시에 유저와의 연결을 끊어버려야 함.
    private ResponseEntity<Map<String, Object>> getUserInfo(String accessToken, String SNSType) {

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
            case "google" -> {
                headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + accessToken);
                userInfoUri = "https://www.googleapis.com/oauth2/v3/userinfo";
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

    // 콜백 이후부터 유저 정보 가져오기 까지의 모든 동작들.
    @Override
    public Map<String, Object> processSNSCert(Map<String, String> params) {

        Map<String, Object> responseBody = new HashMap<>();
        Map<String, Object> accessTokenResponseBody;
        
        // 디코딩 작업.
        Map<String, String> authorizationCodeMap = this.getAuthorizationCodeAndSNSType(params);
        if (authorizationCodeMap.get("cancel").equals("true")) {
            responseBody.put("cancel", "redirect:/login/form");
            return responseBody;
        }
        String SNSType = authorizationCodeMap.get("SNSType");
        
        try {
            // 인증 코드로 토큰 받기.
            ResponseEntity<Map<String, Object>> accessTokenResponse = this.getAccessToken(authorizationCodeMap);
            accessTokenResponseBody = accessTokenResponse.getBody();
            assert accessTokenResponseBody != null;
        } catch (Exception e) {
            log.error(e.getMessage());
            responseBody.put("error", "redirect:/login/form");
            return responseBody;
        }

        String accessToken = (String) accessTokenResponseBody.get("access_token");
        if (accessToken == null) {
            log.info("Token case google...");
            accessToken = (String) accessTokenResponseBody.get("token"); // google 은 이름이 다름.
        }

        assert accessToken != null;
        log.info("토큰 받기 성공={}", accessToken);
        
        try {
            // 토큰으로 사용자 정보 조회.
            ResponseEntity<Map<String, Object>> result = this.getUserInfo(accessToken, SNSType);
            log.info("result={}", result);
            responseBody = result.getBody();
            assert responseBody != null;
        } catch (Exception e) {
            log.error(e.getMessage());
            responseBody.put("error", "redirect:/login/form");
            return responseBody;
        }
        responseBody.put("accessToken", accessToken);
        responseBody.put("SNSType", SNSType);
        responseBody.put("cancel", null);
        responseBody.put("error", null);
        return responseBody;
    }

    // 회원 연동정보 해제.
    private void unlinkUserCert(CertInfo certInfo) {

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
            case "GOOGLE" -> {
                HttpHeaders headers = new HttpHeaders();
                headers.add("content-type", "application/x-www-form-urlencoded");
                unlinkURL = UriComponentsBuilder
                        .fromHttpUrl("https://oauth2.googleapis.com/revoke")
                        .queryParam("token", certInfo.getAccessToken())
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
        }
        try {
            assert response != null;
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("회원 연동정보 해제 성공...");
            } else {
                throw new RestClientException("연동정보 해제 실패...");
            }
        } catch (RestClientException e) {
            log.error("회원 연동정보 해제 실패: {}", response.getStatusCode());
        }
    }

    // 유저 정보 자동입력 메서드.
    public String fillOutSignupForm(Map<String, Object> responseBody, String SNSType, CertInfo certInfo, Model model) {
        switch (SNSType) {
            case "payco" -> {
                // 페이코는 연동 해제 주소가 따로 없음.
                // signupService.unlinkUserCert(certInfo);
                Map<String, Map<String, String>> data = (Map<String, Map<String, String>>) responseBody.get("data");
                Map<String, String> member = data.get("member");
                // 유저 중복 검사.
                String customerPhone = member.get("mobile");
                String email = member.get("email");
                if (this.isExistingCustomers(email, customerPhone)) { // 중복 유저가 존재하는 경우.
                    String encodedMessage = URLEncoder.encode("이미 해당 정보로 가입한 유저가 있습니다.", StandardCharsets.UTF_8);
                    return "redirect:/login/form?error="+encodedMessage;
                };
                model.addAttribute("customerName", member.get("name"));
                model.addAttribute("customerPhone", customerPhone);
                model.addAttribute("email", email);
                model.addAttribute("birthday", member.get("birthday"));
            }
            // 카카오 API를 통해 정보를 받아옴.
            case "kakao" -> {
                certInfo.setTargetIdType("user_id");
                Long userId = (Long) responseBody.get("id");
                certInfo.setTargetId(userId);
                this.unlinkUserCert(certInfo);
                Map<String, String> kakaoAccount = (Map<String, String>) responseBody.get("kakao_account");
                // 유저 중복 검사.
                String phoneNumber = kakaoAccount.get("phone_number");
                String replacedPhoneNumber1 = phoneNumber.replace("+82 ", "0");
                String replacedPhoneNumber2 = replacedPhoneNumber1.replace("-", "");
                String email = kakaoAccount.get("email");
                if (this.isExistingCustomers(email, replacedPhoneNumber2)) { // 중복 유저가 존재하는 경우.
                    String encodedMessage = URLEncoder.encode("이미 해당 정보로 가입한 유저가 있습니다.", StandardCharsets.UTF_8);
                    return "redirect:/login/form?error="+encodedMessage;
                };
                model.addAttribute("customerName", kakaoAccount.get("name"));
                model.addAttribute("customerPhone", replacedPhoneNumber2);
                model.addAttribute("email", email);
                model.addAttribute("birthday", kakaoAccount.get("birthyear") + kakaoAccount.get("birthday"));
            }
            case "naver" -> {
                this.unlinkUserCert(certInfo);
                Map<String, String> response = (Map<String, String>) responseBody.get("response");
                // 유저 중복 검사.
                String mobile = response.get("mobile");
                String replacedMobile = mobile.replace("-", "");
                String email = response.get("email");
                if (this.isExistingCustomers(email, replacedMobile)) { // 중복 유저가 존재하는 경우.
                    String encodedMessage = URLEncoder.encode("이미 해당 정보로 가입한 유저가 있습니다.", StandardCharsets.UTF_8);
                    return "redirect:/login/form?error="+encodedMessage;
                };
                model.addAttribute("customerName", response.get("name"));
                model.addAttribute("customerPhone", replacedMobile);
                model.addAttribute("email", email);
                String birthday = response.get("birthday");
                String replacedBirthday = birthday.replace("-", "");
                model.addAttribute("birthday", response.get("birthyear") + replacedBirthday);
            }
            case "google" -> {
                this.unlinkUserCert(certInfo);
                // 유저 중복 검사.
                String email = (String) responseBody.get("email");
                if (this.isExistingCustomers(email, null)) { // 중복 유저가 존재하는 경우.
                    String encodedMessage = URLEncoder.encode("이미 해당 정보로 가입한 유저가 있습니다.", StandardCharsets.UTF_8);
                    return "redirect:/login/form?error="+encodedMessage;
                };
                model.addAttribute("customerName", responseBody.get("given_name"));
                model.addAttribute("email", email);
            }
            default -> {
                String encodedMessage = URLEncoder.encode("SNS 타입이 지정되지 않았습니다.", StandardCharsets.UTF_8);
                return "redirect:/login/form?error="+encodedMessage;
            }
        }
        return "account/signup/form";
    }
    
    // 일반 회원가입 관련.
    // ---------------------------------------------------------------------------
    // 아이디 존재 여부 확인.
    @Override
    public ResponseEntity<AccountResponse> isExistingRolesUserName(String userName) {
        Roles roles = rolesRepository.findByUserName(userName);
        AccountResponse accountResponse = new AccountResponse("guest", "이미 존재하는 ID.");
        if (roles == null) {
            accountResponse.setMessage("가입 가능한 ID.");
            return new ResponseEntity<>(accountResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
    }

    // 이메일 혹은 휴대폰번호로 검색해서 중복되는 유저의 존재 여부 확인.
    private boolean isExistingCustomers(String email, String customerPhone) {
        log.info("중복유저 검사... email={} | customerPhone={} ", email, customerPhone);
        List<User> userList = userRepository.findByEmailOrCustomerPhone(email, customerPhone);
        log.info("중복 유저 리스트... userList={}", userList);
        return !(userList.isEmpty());
    }

    // 일반 회원 가입.
    @Override
    @Transactional
    public ResponseEntity<AccountResponse> signupCustomer(Map<String, String> userData) {
        AccountResponse accountResponse = new AccountResponse("guest", null);
        String email = userData.get("email");
        String customerPhone = userData.get("customerPhone");

        // 중복 유저가 존재하는 경우.
        if (this.isExistingCustomers(email, customerPhone)) {
            accountResponse.setMessage("이미 해당 정보(휴대폰 번호 혹은 이메일)로 가입한 유저가 있습니다.");
            log.error("회원가입 실패... email={} | customerPhone={}", email, customerPhone);
            return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            // 유저 상세 데이터 기록.
            Roles roles = this.recordRoles(userData);
            this.recordUser(userData, roles);
            // DB 기록 종료.
        } catch (Exception e) {
            accountResponse.setMessage("회원가입 실패: "+e.getMessage());
            log.error("회원가입 실패... Exception={}", e.getMessage());
            return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        accountResponse.setMessage("회원가입 성공.");
        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
    }

    private Roles recordRoles(Map<String, String> userData) {
        try {
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
            return roles;
        } catch (Exception e) {
            log.error("유저 권한 테이블 기록 실패... Exception={}", e.getMessage());
            return null;
        }
    };

    private void recordUser(Map<String, String> userData, Roles roles) throws Exception {
        try {
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
        } catch (Exception e) {
            log.error("유저 세부 정보 기록 실패... Exception={}", e.getMessage());
            throw new Exception("유저 세부 정보 기록 실패.");
        }
    }
}
