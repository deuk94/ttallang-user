package com.ttallang.user.security.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttallang.user.security.config.auth.PrincipalDetails;
import com.ttallang.user.security.response.SecurityResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class LoginHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if (role.equals("ROLE_ADMIN")) {
            response.setContentType("application/json;charset=UTF-8");
            SecurityResponse securityResponse = new SecurityResponse(200, "success","admin", "관리자 로그인 성공.");
            new ObjectMapper().writeValue(response.getWriter(), securityResponse);
        } else if (role.equals("ROLE_USER")) {
            PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (pds.getPaymentStatus().equals("1")) {
                response.setContentType("application/json;charset=UTF-8");
                SecurityResponse securityResponse = new SecurityResponse(200, "success", "user", "유저 로그인 성공.");
                new ObjectMapper().writeValue(response.getWriter(), securityResponse);
            } else {
                response.setContentType("application/json;charset=UTF-8");
                SecurityResponse securityResponse = new SecurityResponse(402, "failure", "user", "미결제 상태.");
                new ObjectMapper().writeValue(response.getWriter(), securityResponse);
            }
        } else { // 로그인이 되었지만 권한을 찾을 수 없으므로 401 처리.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            SecurityResponse securityResponse = new SecurityResponse(401, "failure", "unknown", "권한 정보를 찾을 수 없습니다.");

            new ObjectMapper().writeValue(response.getWriter(), securityResponse);
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

        SecurityResponse securityResponse = new SecurityResponse();
        securityResponse.setCode(401);
        securityResponse.setStatus("failure");
        securityResponse.setRole("guest");
        if (exception.getMessage().contains("자격 증명에 실패하였습니다.")) {
            System.out.println(exception.getMessage());
            securityResponse.setMessage("비밀번호를 확인해주세요.");
        } else if (exception.getMessage().contains("사용자 계정의 유효 기간이 만료 되었습니다.")) {
            System.out.println(exception.getMessage());
            securityResponse.setMessage("탈퇴된 회원입니다.");
        } else if (exception.getMessage().contains("유효하지 않은 사용자입니다.")) {
            System.out.println(exception.getMessage());
            securityResponse.setMessage("존재하지 않는 회원입니다.");
        } else {
            System.out.println(exception.getMessage());
            securityResponse.setMessage("로그인에 실패하였습니다.");
        }

        new ObjectMapper().writeValue(response.getWriter(), securityResponse);
    }
}