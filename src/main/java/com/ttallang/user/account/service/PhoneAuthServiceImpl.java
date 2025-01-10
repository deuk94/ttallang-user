package com.ttallang.user.account.service;

import com.ttallang.user.account.model.AccountResponse;
import com.ttallang.user.security.config.token.RandomAuthNumber;
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
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class PhoneAuthServiceImpl implements PhoneAuthService {

    private final Map<String, String> sharedPhoneAuthNumberMap;

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

    public PhoneAuthServiceImpl(Map<String, String> sharedPhoneAuthNumberMap) {
        this.sharedPhoneAuthNumberMap = sharedPhoneAuthNumberMap;
    }

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(coolsmsKey, coolsmsSecret, "https://api.coolsms.co.kr");
    }

    // 인증용 공유메모리에 대조용 인증번호를 저장하고 성공 여부를 알려주는 메서드.
    @Override
    public boolean isAuthNumberStoredInSharedMap(String to, String authNumber, Map<String, String> sharedMap) {
        String nullAuthNumber; // null 이어야 함
        nullAuthNumber = sharedMap.putIfAbsent(to, authNumber);
        log.info("nullAuthNumber={} | return={}", nullAuthNumber, nullAuthNumber == null);
        // 저장 결과가 null 이 아니라면 이미 저장된 값이 있는것이므로 인증정보 추가 실패이고 그렇지 않으면 성공.
        return nullAuthNumber == null; // 어쨌든 null 이어야 성공한것임.
    }

    // 사용자에게 인증 번호를 보내는 메서드.
    @Override
    public boolean isSMSSentSuccess(String to, String authNumber) {
        try {
            Message message = new Message();

            // 형식에 맞게 만들어서 보내기.
            message.setFrom(coolsmsSender); // 발신자 번호 설정.
            message.setTo(to); // 수신자 번호 설정.
            message.setText("[딸랑이] 인증번호는 [" + authNumber + "] 입니다."); // 메시지 내용 설정.

            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message)); // 메시지 발송 요청을 하는 것으로 메세지가 보내진다.
            log.info("response={}", response); // 응답이 200이 와야함.

            return true;
        } catch (Exception e) {
            log.error("SMS 전송 에러: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public ResponseEntity<AccountResponse> startPhoneAuth(Map<String, String> requestBody) {
        AccountResponse accountResponse = new AccountResponse("guest", null);
        RandomAuthNumber randomAuthNumber = new RandomAuthNumber();
        String authNumber = randomAuthNumber.getRandomAuthNumber();
        String customerPhone = requestBody.get("customerPhone");
        // 인증번호 저장먼저 하고 그 다음 보내기.
        if (this.isAuthNumberStoredInSharedMap(customerPhone, authNumber, sharedPhoneAuthNumberMap)) { // 인증번호 저장 성공한 경우.
            if (this.isSMSSentSuccess(customerPhone, authNumber)) { // 여기서 인증번호를 보냄. true | false.
                accountResponse.setMessage("전송 성공.");
                return new ResponseEntity<>(accountResponse, HttpStatus.OK);
            } else { // 보내기 실패.
                accountResponse.setMessage("서버 에러로 인하여 인증이 취소되었습니다.\n관리자에게 문의해주세요.");
                // 잔액 부족으로 인한 인증 취소일 가능성이 높음.
                sharedPhoneAuthNumberMap.remove(customerPhone); // 서버측 에러로 인한 인증 취소라서 저장했던 인증번호를 지워버림.
                return new ResponseEntity<>(accountResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            accountResponse.setMessage("이미 인증이 진행중입니다.\n인증 번호를 다시 확인해주세요.");
            return new ResponseEntity<>(accountResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    // 사용자로부터 입력받은 인증 번호를 검사하여 결과를 반환하는 메서드.
    public boolean isCorrectAuthNumber(Map<String, String> requestBody, Map<String, String> sharedMap) {
        // 인증번호를 requestBody 에서 받으면 그것을 sharedMap 에서 같은 key 값으로 꺼내와서 대조하고 결과를 반환.
        String customerPhone = requestBody.get("customerPhone");
        String authNumber1 = requestBody.get("authNumber"); // 입력받은 인증번호.
        String authNumber2 = sharedMap.get(customerPhone); // 공유메모리에 저장돼있던 대조용 인증번호.
        return authNumber1.equals(authNumber2); // 일치 여부를 리턴.
    };

    @Override
    public ResponseEntity<AccountResponse> getPhoneAuthResult(Map<String, String> requestBody) {
        AccountResponse accountResponse = new AccountResponse("guest", null);
        String customerPhone = requestBody.get("customerPhone");

        try {
            if (this.isCorrectAuthNumber(requestBody, sharedPhoneAuthNumberMap)) { // 인증번호가 일치한다면,
                sharedPhoneAuthNumberMap.remove(customerPhone); // 임시 인증정보를 지운다.

                accountResponse.setMessage("인증 성공.");
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
}
