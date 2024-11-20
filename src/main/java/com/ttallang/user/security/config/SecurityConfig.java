package com.ttallang.user.security.config;


import com.ttallang.user.security.config.filter.NgrokRedirectFilter;
import com.ttallang.user.security.config.handler.LoginHandler;
import com.ttallang.user.account.model.CertInfo;
import com.ttallang.user.security.config.handler.SessionHandler;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
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
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    private final LoginHandler loginHandler;
    private final SessionHandler sessionHandler;
    private final NgrokRedirectFilter ngrokRedirectFilter;

    public SecurityConfig(
            LoginHandler loginHandler,
            NgrokRedirectFilter ngrokRedirectFilter,
            SessionHandler sessionHandler
    ) {
        this.loginHandler = loginHandler;
        this.sessionHandler = sessionHandler;
        this.ngrokRedirectFilter = ngrokRedirectFilter;
    }

    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(ngrokRedirectFilter, SecurityContextPersistenceFilter.class
                ).cors(AbstractHttpConfigurer::disable
                ).csrf(AbstractHttpConfigurer::disable
                ).sessionManagement(session -> session
                        .sessionFixation()
                        .changeSessionId()
                        .maximumSessions(1)
                        .expiredSessionStrategy(sessionHandler)
                        .maxSessionsPreventsLogin(false)
                        .sessionRegistry(sessionRegistry()))
                .authorizeHttpRequests(auth -> auth
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

    @Bean
    public static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    // 유저 인증 정보를 임시로 저장하는 곳.
    @Bean
    public Map<String, CertInfo> sharedCertInfoMap() {
        return new ConcurrentHashMap<>();
    }

    // SMS 인증 관련 임시 저장소 목록.
    // userName 찾기 관련.
    @Bean
    public Map<String, String> sharedUserNameAuthNumberMap() {
        return new ConcurrentHashMap<>();
    }

    // password 찾기 관련.
    @Bean
    public Map<String, String> sharedPasswordAuthNumberMap() {
        return new ConcurrentHashMap<>();
    }

    // password 찾기 관련 state code 임시 저장.
    @Bean
    public Map<String, String> sharedStateMap() {
        return new ConcurrentHashMap<>();
    }
}
