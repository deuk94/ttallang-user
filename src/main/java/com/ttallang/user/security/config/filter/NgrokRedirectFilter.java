package com.ttallang.user.security.config.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component  // 빈에서 관리하기.
public class NgrokRedirectFilter extends OncePerRequestFilter {

    //@Value("${base.url.localhost}")
    private String localHost;

    // 만약 ngrok으로 리다이렉트 된다면 이 필터가 로컬호스트로 돌려놓을것임.
    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getServerName().endsWith("ngrok-free.app")) {
            String targetUrl = "http://"+localHost+":8080" +
                    request.getRequestURI() +
                    (request.getQueryString() != null ? "?" + request.getQueryString() : "");

            log.info("ngrok 주소는 못씀 -> {}", targetUrl);
            response.sendRedirect(targetUrl);
            return;
        }
        filterChain.doFilter(request, response);
    }
}