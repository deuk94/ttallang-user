package com.ttallang.user.security.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttallang.user.account.model.AccountResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtLogoutHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        AccountResponse accountResponse = new AccountResponse("user", "로그아웃 실패.");
        response.setContentType("application/json;charset=UTF-8");

        try {
            Cookie cookie = new Cookie("access_token", null); // 액세스 토큰 삭제.
            cookie.setHttpOnly(true); // 클라이언트 측에서 접근 못하게 막음.
            cookie.setSecure(true); // https 로만 쿠키 전송, http 에서는 안될 수 있음.
            cookie.setPath("/"); // 애플리케이션 전역에서 접근 가능하도록 설정.
            cookie.setMaxAge(0); // 쿠키 만료시켜버림.

            response.addCookie(cookie);

            response.setStatus(HttpServletResponse.SC_OK);
            accountResponse.setMessage("로그아웃 성공.");
            new ObjectMapper().writeValue(response.getWriter(), accountResponse);
        } catch (IOException ioException) {
            new ObjectMapper().writeValue(response.getWriter(), accountResponse);
        }
    };
}
