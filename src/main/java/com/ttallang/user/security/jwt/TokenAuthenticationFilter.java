package com.ttallang.user.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Service
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private TokenUtil tokenUtil;

    public TokenAuthenticationFilter(TokenUtil tokenUtil) {
        this.tokenUtil = tokenUtil;
    }

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        /*
        * 매 요청마다 헤더를 확인해서 토큰이 없다면 비로그인 유저이고,
        * 토큰이 있다면 로그인 유저임.
        * 토큰이 있어도 권한이 USER 일 수 있고 ADMIN 일 수 있음.
        * 다만 유저의 접근이 있을 때마다 매번 확인해야 하므로 필터로 등록해야 함.
        * */

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) { // token이 null이 아니면서 동시에 'Bearer ' 방식인 토큰만 검증한다.
            token = token.substring(7); // Bearer 6글자에 띄어쓰기 한 칸 까지 포함한 간격.
            String username = tokenUtil.validateToken(token);
            if (username != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
