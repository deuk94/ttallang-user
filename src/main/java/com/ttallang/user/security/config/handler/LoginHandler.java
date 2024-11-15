package com.ttallang.user.security.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttallang.user.security.config.auth.PrincipalDetails;
import com.ttallang.user.account.model.AccountResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        response.setContentType("application/json;charset=UTF-8");
        AccountResponse accountResponse = new AccountResponse("unknown", "권한 정보를 찾을 수 없습니다.");
        if (role.equals("ROLE_ADMIN")) {
            response.setStatus(HttpServletResponse.SC_OK);
            accountResponse.setRole("admin");
            accountResponse.setMessage("관리자 로그인 성공.");
            new ObjectMapper().writeValue(response.getWriter(), accountResponse);
        } else if (role.equals("ROLE_USER")) {
            PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            accountResponse.setRole("user");
            if (pds.getPaymentStatus().equals("1")) {
                response.setStatus(HttpServletResponse.SC_OK);
                accountResponse.setMessage("유저 로그인 성공.");
                new ObjectMapper().writeValue(response.getWriter(), accountResponse);
            } else {
                response.setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED);
                accountResponse.setMessage("마지막 대여 이후 미결제 상태입니다.");
                new ObjectMapper().writeValue(response.getWriter(), accountResponse);
            }
        } else { // 로그인이 되긴 했지만 권한이 잘못되었으므로 403 처리.
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            accountResponse.setMessage("잘못된 권한입니다.");
            new ObjectMapper().writeValue(response.getWriter(), accountResponse);
        }
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        AccountResponse accountResponse = new AccountResponse("guest", null);
        String errorMessage = exception.getMessage();
        switch (errorMessage) {
            case "자격 증명에 실패하였습니다." ->
                    accountResponse.setMessage("비밀번호를 확인해주세요.");
            case "사용자 계정의 유효 기간이 만료 되었습니다." -> // isAccountNonExpired 관련.
                    accountResponse.setMessage("탈퇴한 회원입니다.");
            case "유효하지 않은 사용자입니다." -> // isEnabled 관련.
                    accountResponse.setMessage("존재하지 않는 회원입니다.");
            default ->
                    accountResponse.setMessage("로그인에 실패하였습니다.");
        }

        new ObjectMapper().writeValue(response.getWriter(), accountResponse);
    }
}