package com.ttallang.user.security.config;


import com.ttallang.user.security.config.handler.LoginHandler;
import com.ttallang.user.account.model.CertInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    private final LoginHandler loginHandler = new LoginHandler();

    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Disable CSRF for this example
        http.csrf(AbstractHttpConfigurer::disable
        ).authorizeHttpRequests(auth -> auth
            .requestMatchers("/login/**", "/signup/**", "/api/oauth2/**").permitAll() // 로그인 페이지, 회원가입 페이지만 퍼밋올.
            .requestMatchers("/user/**", "/map/**", "/pay/**", "/myPage/**", "api/pay/**", "api/myPage/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().permitAll()
        ).formLogin(form -> form
            .loginPage("/login/form") // 로그인 페이지.
            .loginProcessingUrl("/api/login") // 로그인 처리할 url.
            .successHandler(loginHandler)
            .failureHandler(loginHandler)
            .permitAll()
        ).securityContext(securityContext -> securityContext
            .requireExplicitSave(false) // 필요한 경우에만 SecurityContext를 세션에 저장.
        ).logout(logout -> logout
            .logoutRequestMatcher(new AntPathRequestMatcher("/api/logout")) // 로그아웃 요청 URL.
            .logoutSuccessUrl("/login/form") // 로그아웃 후 이동할 URL.
            .invalidateHttpSession(true) // 세션 무효화.
            .permitAll()
        );

        // Configure exception handling
        http.exceptionHandling(exceptionHandling -> exceptionHandling
            .authenticationEntryPoint(authenticationEntryPoint())
        );

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            String uri = request.getRequestURI();
            response.sendRedirect("/login/form");
        };
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    // 유저 인증 정보를 임시로 저장하는 곳.
    @Bean
    public Map<String, CertInfo> sharedCertInfoMap() {
        return new ConcurrentHashMap<>();
    }

    // SMS 인증 정보를 임시로 저장하는 곳.
    @Bean
    public Map<String, String> sharedAuthNumberMap() {
        return new ConcurrentHashMap<>();
    }
}
