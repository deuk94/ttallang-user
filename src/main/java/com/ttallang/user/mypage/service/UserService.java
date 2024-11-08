package com.ttallang.user.mypage.service;

import com.ttallang.user.commonModel.Roles;
import com.ttallang.user.commonModel.User;
import com.ttallang.user.mypage.model.JoinUser;
import com.ttallang.user.commomRepository.RolesRepository;
import com.ttallang.user.commomRepository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;

    public UserService(UserRepository userRepository, RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
    }

    // 회원 정보 조회
    public JoinUser getByUserId(int customerId) {
        return userRepository.findByUser(customerId);
    }

    // 회원 역할 조회
    public Roles getByRoleId(int userId) {
        return rolesRepository.findById(userId).orElse(null);
    }

    // 회원 정보 수정
    public User updateUser(int customerId, User updateUser) {
        User user = userRepository.findById(customerId).orElse(null);
        user.setBirthday(updateUser.getBirthday());
        user.setEmail(updateUser.getEmail());
        user.setCustomerPhone(updateUser.getCustomerPhone());
        return userRepository.save(user);
    }

    // 회원 탈퇴
    public Roles deleteUser(int userId) {
        Roles roles = getByRoleId(userId);
        roles.setUserStatus("0");
        return rolesRepository.save(roles);
    }
}
