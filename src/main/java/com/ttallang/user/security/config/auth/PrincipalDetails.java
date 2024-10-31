package com.ttallang.user.security.config.auth;

import com.ttallang.user.commonModel.Roles;
import java.util.ArrayList;
import java.util.Collection;

import com.ttallang.user.commonModel.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

//시큐리티가 /login 주소를 낚아채서 로그인 진행, 로그인 완료 대면 session을 만들어
//Security ContextHolder 키 값에 정보 저장
//오브젝트 타입 => Authentication 객체 => user 정보
//user 타입 => userDetail 타입
//Security session 에는 Authentication 객체만 가능 => user 정보는 UserDetails(PrincipleDetails) 타입
public class PrincipalDetails implements UserDetails {

    private final Roles roles;
    private final User user;

    public PrincipalDetails(Roles roles, User user) {
        this.roles = roles;
        this.user = user;
    }

    //해당 유저의 권한 리턴 하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add((GrantedAuthority) () -> roles.getUserRole());
        return collect;
    }

    // 유저의 실제 PK.
    public int getCustomerID() {
        return user.getCustomerId();
    }

    @Override
    public String getPassword() {
        return roles.getUserPassword();
    }

    @Override
    public String getUsername() {
        return roles.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !roles.getUserStatus().equals("0"); // "2"인경우 블랙리스트로 취급할까?
    }
}
