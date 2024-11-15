package com.ttallang.user.account.service;

import com.ttallang.user.account.model.AccountResponse;
import com.ttallang.user.commomRepository.RolesRepository;
import com.ttallang.user.commomRepository.UserRepository;
import com.ttallang.user.commonModel.Roles;
import com.ttallang.user.security.config.token.RandomAuthNumber;
import com.ttallang.user.account.model.RolesUser;
import com.ttallang.user.security.config.token.RandomStateToken;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
public class FindServiceImpl implements FindService {

    // 의존성 주입.
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final Map<String, String> sharedUserNameAuthNumberMap;
    private final Map<String, String> sharedPasswordAuthNumberMap;
    private final Map<String, String> sharedStateMap;
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // 암호화해주는놈.

    private DefaultMessageService messageService;

    // 민감 정보 변수화.
    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------
    @Value("${coolsms.key}")
    private String coolsmsKey;

    @Value("${coolsms.secret}")
    private String coolsmsSecret;

    @Value("${coolsms.sender}")
    private String coolsmsSender;
    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------

    public FindServiceImpl(
            UserRepository userRepository,
            RolesRepository rolesRepository,
            Map<String, String> sharedUserNameAuthNumberMap,
            Map<String, String> sharedPasswordAuthNumberMap,
            Map<String, String> sharedStateMap,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.sharedUserNameAuthNumberMap = sharedUserNameAuthNumberMap;
        this.sharedPasswordAuthNumberMap = sharedPasswordAuthNumberMap;
        this.sharedStateMap = sharedStateMap;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(coolsmsKey, coolsmsSecret, "https://api.coolsms.co.kr");
    }

    @Override
    public ResponseEntity<AccountResponse> findUserName(Map<String, String> requestBody) {
        return this.getResponseEntity(requestBody, "userName");
    };

    @Override
    public ResponseEntity<AccountResponse> findPassword(Map<String, String> requestBody) {
        return this.getResponseEntity(requestBody, "password");
    }

    @Override
    // 사용자로부터 입력받은 인증 번호를 서버측과 대조하여 결과를 반환하는 메서드.
    public ResponseEntity<AccountResponse> checkAuthNumber(Map<String, String> requestBody, String findType) {
        AccountResponse accountResponse = new AccountResponse("guest", "인증 번호가 다릅니다.");
        String userName = requestBody.get("userName");
        String customerPhone = requestBody.get("customerPhone");
        String authNumber1 = requestBody.get("authNumber"); // 입력받은 인증번호.
        Map<String, String> sharedMap;
        RolesUser rolesUser;
        String message;
        String target = null;
        String state = null;;
        try {
            if (findType.equals("userName")) {
                sharedMap = sharedUserNameAuthNumberMap;
                System.out.println("11111");
                System.out.println(customerPhone);
                rolesUser = findByCustomerPhone(customerPhone);
                message = rolesUser.getUserName(); // 이미 맵에서 userName을 받고 있는데 한 번 더 찾을 필요가 있는지 모르겠음.
            } else if (findType.equals("password")) {
                assert userName != null; // null이면 안됨.
                sharedMap = sharedPasswordAuthNumberMap;
                System.out.println("22222");
                System.out.println(userName);
                System.out.println(customerPhone);
                rolesUser = findByUserNameAndCustomerPhone(userName, customerPhone);
                target = rolesUser.getUserName();
                // 패스워드의 경우는 클라이언트 측에서 메세지를 받아들이고 처리하는 로직이 아이디 처리 쪽과 다르다.
                RandomStateToken randomStateToken = new RandomStateToken(target);
                state = randomStateToken.getRandomStateToken();
                message = state;
            } else {
                accountResponse.setMessage("인증 도중 에러가 발생하였습니다.");
                return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            assert sharedMap != null;
            String authNumber2 = sharedMap.get(customerPhone); // 공유메모리에 저장돼있던 대조용 인증번호.
            if (authNumber1.equals(authNumber2)) { // 인증번호가 일치한다면,
                accountResponse.setMessage(message);
                if (target != null && state != null) {
                    sharedStateMap.putIfAbsent(target, state); // state를 등록한다.
                }
                sharedMap.remove(customerPhone); // 임시 인증정보를 지운다.
                return new ResponseEntity<>(accountResponse, HttpStatus.OK); // 200.
            } else { // 인증 번호가 다름.
                return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST); // 400.
            }
        } catch (Exception e) {
            accountResponse.setMessage("인증 도중 에러가 발생하였습니다.");
            log.error("SMS 인증 에러: {}", e.getMessage());
            return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR); // 500.
        }
    };

    @Override
    public String renderPasswordChangePage(String state, Model model) {
        try {
            String[] stateParts = state.split(":");
            String target = stateParts[1];
            byte[] decodedBytes = Base64.getUrlDecoder().decode(target);
            String decodedUserName = new String(decodedBytes);
            String originalState = sharedStateMap.get(decodedUserName);
            if (originalState != null) { // 유효한 인증이면서,
                if (originalState.equals(state)) { // 검증 코드도 일치하면,
                    sharedStateMap.remove(decodedUserName); // 다른 접근을 하지 못하도록 state 바로 지워버림.
                    model.addAttribute("userName", decodedUserName);
                    return "account/find/changePassword";
                } else { // 검증 코드 불일치는 사용자가 임의의 값으로 접근한 경우임.
                    String encodedMessage = URLEncoder.encode("잘못된 접근입니다!!!", StandardCharsets.UTF_8);
                    return "redirect:/login/form?error="+encodedMessage;
                }
            } else { // 맵에 값이 없다는 것은 유효하지 않다는 뜻.
                String encodedMessage = URLEncoder.encode("검증 값이 유효하지 않습니다.", StandardCharsets.UTF_8);
                return "redirect:/login/form?error="+encodedMessage;
            }
        } catch (IllegalArgumentException e) { // 임의 값을 넣었을 때 디코딩 에러가 난 경우.
            String encodedMessage = URLEncoder.encode("잘못된 접근입니다!!!", StandardCharsets.UTF_8);
            return "redirect:/login/form?error="+encodedMessage;
        } catch (Exception e) { // 나도 모르는 서버 에러가 난 경우.
            String encodedMessage = URLEncoder.encode("서버 에러가 발생하였습니다.\n관리자에게 문의해주세요.", StandardCharsets.UTF_8);
            return "redirect:/login/form?error="+encodedMessage;
        }
    }

    @Override
    public ResponseEntity<AccountResponse> changePassword(Map<String, String> requestBody) {
        AccountResponse accountResponse = new AccountResponse("guest", null);
        try {
            String userName = requestBody.get("userName");
            String rawPassword = requestBody.get("userPassword");

            String encPassword = bCryptPasswordEncoder.encode(rawPassword);
            Roles roles = rolesRepository.findByUserName(userName);
            roles.setUserPassword(encPassword);
            rolesRepository.save(roles);

            accountResponse.setMessage("성공적으로 변경되었습니다.");
            return new ResponseEntity<>(accountResponse, HttpStatus.OK);
        } catch (Exception e) {
            accountResponse.setMessage("변경 도중 에러가 발생하였습니다.");
            return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 아래부턴 서비스에서 내부적으로만 사용하는 메서드들. 외부에서 접근할 수 없음.

    // 사용자에게 인증 번호를 보내기 시도하고 성공 여부를 응답하는 메서드.
    private ResponseEntity<AccountResponse> getResponseEntity(Map<String, String> requestBody, String findType) {
        AccountResponse accountResponse = new AccountResponse("guest", null);
        String userName = requestBody.get("userName");
        String customerPhone = requestBody.get("customerPhone");
        RolesUser rolesUser;
        System.out.println(findType);
        try {
            if (findType.equals("userName")) {
                rolesUser = this.findByCustomerPhone(customerPhone);
            } else if (findType.equals("password")) {
                assert userName != null;
                System.out.println("들어옴");
                rolesUser = this.findByUserNameAndCustomerPhone(userName, customerPhone);
            } else {
                accountResponse.setMessage("인증 도중 에러가 발생하였습니다.");
                return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            System.out.println(rolesUser.toString());
            if (rolesUser != null) { // 유저가 있긴 함.
                Roles roles = rolesRepository.findByUserName(rolesUser.getUserName());
                String userStatus = roles.getUserStatus();
                if (userStatus.equals("0")) { // 이미 탈퇴한 유저임.
                    accountResponse.setMessage("탈퇴한 유저입니다.");
                    sharedPasswordAuthNumberMap.remove(customerPhone); // 탈퇴 유저이기 때문에 인증정보를 지운다.
                    return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST);
                } else if (userStatus.equals("1")) {
                    int sendSMSResult = this.sendSMS(customerPhone, findType);
                    if (sendSMSResult == 0) { // 보내기 시도.
                        accountResponse.setMessage("이미 인증이 진행중입니다.");
                        return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST);
                    } else if (sendSMSResult == -1) {
                        accountResponse.setMessage("서버 에러로 인하여 인증이 취소되었습니다.\n관리자에게 문의해주세요.");
                        // 잔액 부족으로 인한 인증 취소일 가능성이 높음.
                        sharedPasswordAuthNumberMap.remove(customerPhone); // 서버측 에러로 인한 인증 취소.
                        return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else { // 탈퇴도 아니고 활성화도 아니고???
                    accountResponse.setMessage("인증 도중 에러가 발생하였습니다.");
                    sharedPasswordAuthNumberMap.remove(customerPhone); // 조건문 오류로 인한 인증 취소.
                    return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else { // 유저가 없음.
                accountResponse.setMessage("일치하는 유저 정보가 없습니다.");
                sharedPasswordAuthNumberMap.remove(customerPhone); // 일치하는 유저 정보가 없어서 인증이 의미없으므로 인증정보를 지운다.
                return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            accountResponse.setMessage("인증 도중 에러가 발생하였습니다.");
            log.error("유저 정보 찾기 에러: {}", e.getMessage());
            sharedPasswordAuthNumberMap.remove(customerPhone); // 에러로 인한 인증 취소.
            return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        accountResponse.setMessage("전송 성공.");
        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
    }

    // 실질적으로 사용자에게 인증 번호를 보내는 메서드.
    private int sendSMS(String to, String findType) {
        try {
            RandomAuthNumber randomAuthNumber = new RandomAuthNumber();
            String authNumber = randomAuthNumber.getRandomAuthNumber();
            // 진행중인 인증정보를 공유메모리에 저장 시도하여 이미 진행중인 인증인지 판단한다.
            // 이 때 진행중인 인증의 종류를 파악하여 접근하는 공유메모리가 달라짐.
            String alreadyNumber = null;
            if (findType.equals("userName")) { // 아이디 찾기.
                alreadyNumber = sharedUserNameAuthNumberMap.putIfAbsent(to, authNumber);
            } else if (findType.equals("password")) { // 비밀번호 찾기.
                alreadyNumber = sharedPasswordAuthNumberMap.putIfAbsent(to, authNumber);
            } else {
                // 있을 수 없는 일이긴 하지만 일단 분기시켜놓음.
                return -1; // 서버측 에러로 처리.
            }
            if (alreadyNumber != null) { // 저장 결과가 null 이 아니라면 이미 저장된 값이 있는것이므로 인증정보 추가 실패.
                System.out.println("추가 실패");
                return 0; // false;
            }
            // 만약 저장 성공하였다면 새로운 인증이므로 진행한다.
            Message message = new Message();

            // 형식에 맞게 만들어서 보내기.
            message.setFrom(coolsmsSender); // 발신자 번호 설정.
            message.setTo(to); // 수신자 번호 설정.
            message.setText("[딸랑이] 인증번호는 [" + authNumber + "] 입니다."); // 메시지 내용 설정.

            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message)); // 메시지 발송 요청을 하는 것으로 메세지가 보내진다.
            System.out.println(response); // 응답이 200이 와야함.
            return 1; // true;
        } catch (Exception e) {
            log.error("error={}", e.getMessage());
            return -1; // false 이긴 한데 추가 실패한 경우와 구분하려고 함.
        }
    }

    private RolesUser findByCustomerPhone(String customerPhone) {
        return userRepository.findByCustomerPhone(customerPhone);
    }

    private RolesUser findByUserNameAndCustomerPhone(String userName, String customerPhone) {
        /*
        사실 이건 필요없는 느낌이 들긴 한다.
        왜냐하면 핸드폰 번호는 실제 세상에서도 유니크하기 때문에 바로 위의 메서드 만으로도 원하는 유저를 찾을 수 있다.
        그리고 마이페이지에서도 핸드폰 번호는 바꿀 수 없다...
        따라서 관리자단에서 유저 핸드폰번호와 유저네임을 다르게 매칭해놓는 경우가 아니라면 이 메서드는 필요가 없긴 하다.
        다만 메서드를 패턴화하면서 무결성 검증도 하고, 이를 통해 로직의 완결성을 보장하고 싶었다.
        */
        return userRepository.findByUserNameAndCustomerPhone(userName, customerPhone);
    }
}
