package com.ttallang.user.userAuth.security.config;


import com.ttallang.user.userAuth.security.config.handler.LoginHandler;
import com.ttallang.user.userAuth.security.service.PaycoOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private PaycoOAuth2UserService paycoOAuth2UserService;

    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Disable CSRF for this example
        http.csrf(AbstractHttpConfigurer::disable
        ).authorizeHttpRequests(auth -> auth
            .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/oauth2/**").permitAll()
            .anyRequest().permitAll()
        ).formLogin(form -> form
            .loginPage("/loginForm") // 로그인 페이지.
            .loginProcessingUrl("/api/login") // 로그인 처리할 url.
            .successHandler((request, response, authentication) -> new LoginHandler().onAuthenticationSuccess(request, response, authentication))
            .failureHandler((request, response, authentication) -> new LoginHandler().onAuthenticationFailure(request, response, authentication))
            .permitAll()
        ).oauth2Login(oauth2 -> oauth2
            .loginPage("/loginForm")
            .userInfoEndpoint(userInfo -> userInfo.userService(paycoOAuth2UserService))
        ).securityContext(securityContext -> securityContext
            .requireExplicitSave(false) // 필요한 경우에만 SecurityContext를 세션에 저장.
        ).logout(logout -> logout
            .logoutRequestMatcher(new AntPathRequestMatcher("/api/logout")) // 로그아웃 요청 URL.
            .logoutSuccessUrl("/loginForm") // 로그아웃 후 이동할 URL.
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
            response.sendRedirect("/loginForm");
        };
    }
}
