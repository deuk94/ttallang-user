package com.ttallang.user.security.config.auth;

import com.ttallang.user.commomRepository.RolesRepository;
import com.ttallang.user.commomRepository.UserRepository;
import com.ttallang.user.commonModel.Roles;
import com.ttallang.user.commonModel.User;
import com.ttallang.user.security.model.PaymentUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//시큐리티 설정에서 .loginProcessingUrl("/login") 요청이 오면
//UserDetailsService 타입으로 Ioc 되어 있는 loadUserByUsername 함수 실행 => 자동
@Service
public class PrincipalDetailsService implements UserDetailsService {

    private final RolesRepository rolesRepository;
    private final UserRepository userRepository;

    public PrincipalDetailsService(RolesRepository rolesRepository, UserRepository userRepository) {
        this.rolesRepository = rolesRepository;
        this.userRepository = userRepository;
    }

    //시큐리티 session(내부 Authentication(내부 UserDetails)) 들어감
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        // userName 은 Roles 테이블에 등록되있는 유저의 아이디 (Unique 해야함.)
        System.out.println("userName="+userName);
        Roles roles = rolesRepository.findByUserName(userName);
        int userId = roles.getUserId();
        if (roles.getUserRole().equals("ROLE_ADMIN")) { // 관리자의 경우는 user(DB 상으로는 customer)를 받지 않음.
            return new PrincipalDetails(roles);
        }
        User user = userRepository.findByUserId(userId);
        PaymentUser paymentUser = userRepository.findNoPaymentUser(user.getCustomerId());
        if (paymentUser == null) { //
            return new PrincipalDetails(roles, user);
        }
        return new PrincipalDetails(roles, user, paymentUser);
    }
}
