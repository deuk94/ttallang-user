package com.ttallang.user.security.service;

import com.ttallang.user.commomRepository.UserRepository;
import com.ttallang.user.security.model.RolesUser;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;

@Service
public class FindServiceImpl implements FindService {

    // 의존성 주입.
    private final UserRepository userRepository;

    // 민감 정보 변수화.
    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------
//    @Value("${coolsms.key}")
    private String coolsmsKey;

//    @Value("${coolsms.secret}")
    private String coolsmsSecret;

//    @Value("${coolsms.sender}")
    private String coolsmsSender;
    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------

    public FindServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean findUserNameByCustomerPhone(String customerPhone) {
        RolesUser rolesUser = userRepository.findUserNameByCustomerPhone(customerPhone);
        return rolesUser != null;
    };

    @Override
    public String sendSms(String to) throws CoolsmsException {
        try {
            // 랜덤한 4자리 인증번호 생성
            String numStr = generateRandomNumber();

            Message coolsms = new Message(coolsmsKey, coolsmsSecret); // 생성자를 통해 API 키와 API 시크릿 전달

            HashMap<String, String> params = new HashMap<>();
            params.put("to", to);    // 수신 전화번호
            params.put("from", coolsmsSender);    // 발신 전화번호
            params.put("type", "sms");
            params.put("text", "인증번호는 [" + numStr + "] 입니다.");

            // 메시지 전송
            coolsms.send(params);

            return numStr; // 생성된 인증번호 반환

        } catch (Exception e) {
            throw new CoolsmsException("Failed to send SMS", 500);
        }
    }

    // 랜덤한 4자리 숫자 생성 메서드
    private String generateRandomNumber() {
        Random rand = new Random();
        StringBuilder numStr = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            numStr.append(rand.nextInt(10));
        }
        return numStr.toString();
    }
}
