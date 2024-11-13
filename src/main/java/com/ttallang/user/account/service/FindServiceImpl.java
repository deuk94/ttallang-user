package com.ttallang.user.account.service;

import com.ttallang.user.commomRepository.UserRepository;
import com.ttallang.user.security.config.token.RandomAuthNumber;
import com.ttallang.user.account.model.RolesUser;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class FindServiceImpl implements FindService {

    // 의존성 주입.
    private final UserRepository userRepository;
    private final Map<String, String> sharedAuthNumberMap;
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

    public FindServiceImpl(UserRepository userRepository, Map<String, String> sharedAuthNumberMap) {
        this.userRepository = userRepository;
        this.sharedAuthNumberMap = sharedAuthNumberMap;
    }

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(coolsmsKey, coolsmsSecret, "https://api.coolsms.co.kr");
    }

    @Override
    public boolean findUserNameByCustomerPhone(String customerPhone) {
        RolesUser rolesUser = userRepository.findUserNameByCustomerPhone(customerPhone);
        return rolesUser != null;
    };

    @Override
    public int sendSMS(String to) {
        try {
            RandomAuthNumber randomAuthNumber = new RandomAuthNumber();
            String number = randomAuthNumber.getRandomAuthNumber();
            // 진행중인 인증정보를 공유메모리에 저장 시도하여 이미 진행중인 인증인지 판단한다.
            String alreadyNumber = sharedAuthNumberMap.putIfAbsent(to, number);
            if (alreadyNumber != null) { // 저장 결과가 null 이 아니라면 이미 저장된 값이 있는것이므로 인증정보 추가 실패.
                System.out.println("추가 실패");
                return 0; // false;
            }
            // 만약 저장 성공하였다면 새로운 인증이므로 진행한다.
            Message message = new Message();

            // 형식에 맞게 만들어서 보내기.
            message.setFrom(coolsmsSender); // 발신자 번호 설정
            message.setTo(to); // 수신자 번호 설정
            message.setText("[딸랑이] 인증번호는 [" + number + "] 입니다."); // 메시지 내용 설정

            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message)); // 메시지 발송 요청
            System.out.println(response);
            return 1; // true;
        } catch (Exception e) {
            log.error("error={}", e.getMessage());
            return -1; // false 이긴 한데 추가 실패한 경우와 구분하려고 함.
        }
    }

    @Override
    public RolesUser getUserNameByCustomerPhone(String customerPhone) {
        return userRepository.findUserNameByCustomerPhone(customerPhone);
    }
}
