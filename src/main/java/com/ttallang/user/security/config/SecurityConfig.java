package com.ttallang.user.security.config;

import com.ttallang.user.security.config.handler.LoginHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Disable CSRF for this example
        http.csrf(csrfConfig -> csrfConfig.disable());

        // Set up access control
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
            .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().permitAll()
        );

        // Configure form login
        http.formLogin(form -> form
            .loginPage("/loginForm")
            .loginProcessingUrl("/login")
            .successHandler(new LoginHandler()::onAuthenticationSuccess)
            .failureHandler(new LoginHandler()::onAuthenticationFailure)
            .permitAll()
        );

        // Configure exception handling
        http.exceptionHandling(exceptionHandling -> exceptionHandling
            .authenticationEntryPoint(authenticationEntryPoint())
        );

        // Configure security context to allow session persistence
        http.securityContext(securityContext -> securityContext
            .requireExplicitSave(false) // 필요한 경우에만 SecurityContext를 세션에 저장
        );


        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.sendRedirect("/loginForm");
        };
    }
}
