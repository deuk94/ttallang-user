package com.ttallang.user.security.config.auth;

import com.ttallang.user.commomRepository.RolesRepository;
import com.ttallang.user.commomRepository.UserRepository;
import com.ttallang.user.commonModel.Roles;
import com.ttallang.user.commonModel.User;
import com.ttallang.user.security.model.NotPaymentUser;
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

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        // userName 은 Roles 테이블에 등록되있는 유저의 아이디 (Unique 해야함.)
        System.out.println("userName="+userName);
        Roles roles = rolesRepository.findByUserName(userName);
        if (roles == null) { // 이 경우는 유저 정보가 아예 없는 경우.
            return new PrincipalDetails(
                    // 시큐리티 콘피그에서 유저 로그인 핸들러를 통해 로그인 실패하는 것을 관리하기 위해 가짜 객체를 만들어줬음.
                    new Roles(0, "", "", "", "-1"),
                    new User(0, 0, "", "", "", "")
            );
        }
        int userId = roles.getUserId();
        User user = userRepository.findByUserId(userId);
        NotPaymentUser notPaymentUser = userRepository.findNotPaymentUser(user.getCustomerId());
        if (notPaymentUser == null) { // 결제를 했기 때문에 null임.
            return new PrincipalDetails(roles, user);
        }
        return new PrincipalDetails(roles, user, notPaymentUser);
    }
}
