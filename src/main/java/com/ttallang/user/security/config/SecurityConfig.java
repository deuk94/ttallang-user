package com.ttallang.user.security.config;


import com.ttallang.user.security.config.auth.PrincipalDetailsService;
import com.ttallang.user.security.config.handler.LoginHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

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
        http.csrf((csrfConfig) -> csrfConfig.disable());
        // Set up access control
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
            .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().permitAll()
        );
        http.formLogin(form -> form
            .loginPage("/loginForm")
            .loginProcessingUrl("/login")
            .successHandler((request, response, authentication) -> new LoginHandler().onAuthenticationSuccess(request, response, authentication))
            .failureHandler((request, response, authentication) -> new LoginHandler().onAuthenticationFailure(request, response, authentication))
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
