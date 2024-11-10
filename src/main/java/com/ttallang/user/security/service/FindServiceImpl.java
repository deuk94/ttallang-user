package com.ttallang.user.security.service;

import com.ttallang.user.commomRepository.UserRepository;
import com.ttallang.user.security.config.RandomAuthNumber;
import com.ttallang.user.security.model.RolesUser;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FindServiceImpl implements FindService {

    // 의존성 주입.
    private final UserRepository userRepository;
    private final Map<String, String> sharedAuthNumberMap;

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

    @Override
    public boolean findUserNameByCustomerPhone(String customerPhone) {
        RolesUser rolesUser = userRepository.findUserNameByCustomerPhone(customerPhone);
        return rolesUser != null;
    };

    @Override
    public boolean sendSms(String to) throws CoolsmsException {
        try {
            RandomAuthNumber randomAuthNumber = new RandomAuthNumber();
            String number = randomAuthNumber.getRandomAuthNumber();
            String alreadyNumber = sharedAuthNumberMap.putIfAbsent(to, number);
            if (alreadyNumber != null) { // null 이 아니라면 추가 실패.
                System.out.println("추가 실패");
                return false;
            }
            Message coolsms = new Message(coolsmsKey, coolsmsSecret); // 생성자를 통해 필수키 기밀정보 전달.
            // 형식에 맞게 만들어서 보내기.
            HashMap<String, String> params = new HashMap<>();
            params.put("to", to);
            params.put("from", coolsmsSender);
            params.put("type", "sms");
            params.put("text", "인증번호는 [" + number + "] 입니다.");
            coolsms.send(params); // 메시지 전송.
            System.out.println("잘 보냄.");
            return true;
        } catch (Exception e) {
            log.error("error={}", e.getMessage());
            return true;
        }
    }

    @Override
    public RolesUser getUserNameByCustomerPhone(String customerPhone) {
        return userRepository.findUserNameByCustomerPhone(customerPhone);
    }
}
