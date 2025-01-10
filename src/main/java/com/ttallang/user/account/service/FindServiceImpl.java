package com.ttallang.user.account.service;

import com.ttallang.user.account.model.AccountResponse;
import com.ttallang.user.commomRepository.RolesRepository;
import com.ttallang.user.commomRepository.UserRepository;
import com.ttallang.user.commonModel.Roles;
import com.ttallang.user.account.model.RolesUser;
import com.ttallang.user.security.config.token.RandomAuthNumber;
import com.ttallang.user.security.config.token.RandomStateToken;
import lombok.extern.slf4j.Slf4j;
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
    private final PhoneAuthService phoneAuthService;

    public FindServiceImpl(
            UserRepository userRepository,
            RolesRepository rolesRepository,
            Map<String, String> sharedUserNameAuthNumberMap,
            Map<String, String> sharedPasswordAuthNumberMap,
            Map<String, String> sharedStateMap,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            PhoneAuthService phoneAuthService
    ) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.sharedUserNameAuthNumberMap = sharedUserNameAuthNumberMap;
        this.sharedPasswordAuthNumberMap = sharedPasswordAuthNumberMap;
        this.sharedStateMap = sharedStateMap;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.phoneAuthService = phoneAuthService;
    }

    @Override
    public ResponseEntity<AccountResponse> findUserName(Map<String, String> requestBody) {
        String customerPhone = requestBody.get("customerPhone");
        RolesUser rolesUser = this.findByCustomerPhone(customerPhone);
        if (rolesUser == null) {
            return new ResponseEntity<>(new AccountResponse("guest", "일치하는 유저 정보가 없습니다."), HttpStatus.BAD_REQUEST);

        }
        return this.getResponseEntity(customerPhone, rolesUser, sharedUserNameAuthNumberMap);
    };

    @Override
    public ResponseEntity<AccountResponse> findPassword(Map<String, String> requestBody) {
        String userName = requestBody.get("userName");
        String customerPhone = requestBody.get("customerPhone");
        RolesUser rolesUser = this.findByUserNameAndCustomerPhone(userName, customerPhone);
        if (rolesUser == null) {
            return new ResponseEntity<>(new AccountResponse("guest", "일치하는 유저 정보가 없습니다."), HttpStatus.BAD_REQUEST);
        }
        return this.getResponseEntity(customerPhone, rolesUser, sharedPasswordAuthNumberMap);
    }

    // 인증용 공유메모리에 대조용 인증번호를 저장하고 성공 여부를 알려주는 메서드.
    @Override
    public boolean isAuthNumberStoredInSharedMapForFind(String to, String authNumber, Map<String, String> sharedMap) {
        String nullAuthNumber; // null 이어야 함
        nullAuthNumber = sharedMap.putIfAbsent(to, authNumber);
        log.info("nullAuthNumber={} | return={}", nullAuthNumber, nullAuthNumber == null);
        // 저장 결과가 null 이 아니라면 이미 저장된 값이 있는것이므로 인증정보 추가 실패이고 그렇지 않으면 성공.
        return nullAuthNumber == null; // 어쨌든 null 이어야 성공한것임.
    }

    @Override
    public ResponseEntity<AccountResponse> getUserNameByCustomerPhone(Map<String, String> requestBody) {
        AccountResponse accountResponse = new AccountResponse("guest", null);

        try {
            if (phoneAuthService.isCorrectAuthNumber(requestBody, sharedUserNameAuthNumberMap)) { // 인증번호가 일치한다면,
                String customerPhone = requestBody.get("customerPhone");
                RolesUser rolesUser = findByCustomerPhone(customerPhone);
                String message = rolesUser.getUserName(); // 클라이언트에게 전달될 결과 메세지. 이것이 곧 찾으려는 UID 임.
                sharedUserNameAuthNumberMap.remove(customerPhone); // 임시 인증정보를 지운다.

                accountResponse.setMessage(message);
                return new ResponseEntity<>(accountResponse, HttpStatus.OK);
            } else {
                accountResponse.setMessage("인증 번호가 다릅니다.");
                return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("SMS username 인증 에러: {}", e.getMessage());

            accountResponse.setMessage("인증 도중 에러가 발생하였습니다.");
            return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<AccountResponse> getPasswordByUserNameAndCustomerPhone(Map<String, String> requestBody) {
        AccountResponse accountResponse = new AccountResponse("guest", null);

        try {
            if (phoneAuthService.isCorrectAuthNumber(requestBody, sharedPasswordAuthNumberMap)) { // 인증번호가 일치한다면,
                String userName = requestBody.get("userName");
                String customerPhone = requestBody.get("customerPhone");
                RolesUser rolesUser = findByUserNameAndCustomerPhone(userName, customerPhone);
                String target = rolesUser.getUserName(); // 패스워드는 전체적으로 처리하는 로직이 아이디 쪽과 다르다. 그래서 message 가 아님.

                RandomStateToken randomStateToken = new RandomStateToken(target);
                String state = randomStateToken.getRandomStateToken();

                sharedStateMap.putIfAbsent(target, state); // state 를 등록한다. password 찾기의 경우 나중에 sharedStateMap 를 다시 참조해서 state 값을 검증해야 함.
                sharedPasswordAuthNumberMap.remove(customerPhone); // 임시 인증정보를 지운다.

                accountResponse.setMessage(state); // 클라이언트에게 전달될 검증값. 나중에 클라이언트로부터 받게 되는 state 가 올바르면 비밀번호 변경 가능.
                return new ResponseEntity<>(accountResponse, HttpStatus.OK);
            } else {
                accountResponse.setMessage("인증 번호가 다릅니다.");
                return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("SMS password 인증 에러: {}", e.getMessage());

            accountResponse.setMessage("인증 도중 에러가 발생하였습니다.");
            return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
                    sharedStateMap.remove(decodedUserName); // state 한 번 사용하고 다른 접근을 다시 하지 못하도록 state 바로 지워버림.
                    model.addAttribute("userName", decodedUserName);
                    model.addAttribute("state", target);
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
            String state = requestBody.get("state");
            log.info("state={}", state);
            String userName = requestBody.get("userName");
            String rawPassword = requestBody.get("userPassword");

            byte[] decodedBytes = Base64.getUrlDecoder().decode(state);
            String decodedUserName = new String(decodedBytes);

            if (state != null && decodedUserName.equals(userName)) {
                String encPassword = bCryptPasswordEncoder.encode(rawPassword);
                Roles roles = rolesRepository.findByUserName(userName);
                roles.setUserPassword(encPassword);
                rolesRepository.save(roles);
                accountResponse.setMessage("성공적으로 변경되었습니다.");
                return new ResponseEntity<>(accountResponse, HttpStatus.OK);
            } else {
                accountResponse.setMessage("검증 값이 일치하지 않습니다.");
                return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            accountResponse.setMessage("변경 도중 에러가 발생하였습니다.");
            return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 사용자에게 인증 번호를 보내기 시도하고 성공 여부를 응답하는 메서드.
    // 인증 번호를 보내는 것은 sendSMS 메서드가 수행하고 아래 메서드는 유저를 찾는 것에 목적을 두었음.
    @Override
    public ResponseEntity<AccountResponse> getResponseEntity(String customerPhone, RolesUser rolesUser, Map<String, String> sharedMap) {
        AccountResponse accountResponse = new AccountResponse("guest", null);
        try {
            Roles roles = rolesRepository.findByUserName(rolesUser.getUserName());
            String userStatus = roles.getUserStatus();
            if (userStatus.equals("0")) { // 이미 탈퇴한 유저임.
                accountResponse.setMessage("탈퇴한 유저입니다.");
                return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST);
            } else if (userStatus.equals("1")) {
                RandomAuthNumber randomAuthNumber = new RandomAuthNumber();
                String authNumber = randomAuthNumber.getRandomAuthNumber();
                // 인증번호 저장먼저 하고 그 다음 보내기.
                if (this.isAuthNumberStoredInSharedMapForFind(customerPhone, authNumber, sharedMap)) { // 인증번호 저장 성공한 경우.
                    if (phoneAuthService.isSMSSentSuccess(customerPhone, authNumber)) { // 여기서 인증번호를 보냄. true | false.
                        accountResponse.setMessage("전송 성공.");
                        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
                    } else { // 보내기 실패.
                        accountResponse.setMessage("서버 에러로 인하여 인증이 취소되었습니다.\n관리자에게 문의해주세요.");
                        // 잔액 부족으로 인한 인증 취소일 가능성이 높음.
                        sharedMap.remove(customerPhone); // 서버측 에러로 인한 인증 취소라서 저장했던 인증번호를 지워버림.
                        return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    accountResponse.setMessage("이미 인증이 진행중입니다.\n인증 번호를 다시 확인해주세요.");
                    return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST);
                }
            } else { // 탈퇴도 아니고 활성화도 아니고???
                accountResponse.setMessage("인증 도중 에러가 발생하였습니다.");
                return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            accountResponse.setMessage("인증 도중 에러가 발생하였습니다.");
            log.error("유저 정보 찾기 에러: {}", e.getMessage());
            sharedMap.remove(customerPhone); // 에러로 인한 인증 취소가 되서 혹시 남아있을지 모를 인증번호 정보를 지움.
            return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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
