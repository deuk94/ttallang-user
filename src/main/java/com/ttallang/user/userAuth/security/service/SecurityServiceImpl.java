package com.ttallang.user.userAuth.security.service;

import com.ttallang.user.commonModel.Roles;
import com.ttallang.user.commonModel.User;
import com.ttallang.user.commonModel.repository.UserRepository;
import com.ttallang.user.userAuth.security.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SecurityServiceImpl implements SecurityService  {
    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder; // 암호화해주는놈.

    @Override
    public boolean isExistingCustomer(String userName) {
        Roles roles = rolesRepository.findByUserName(userName);
        return roles != null;
    }

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

}
