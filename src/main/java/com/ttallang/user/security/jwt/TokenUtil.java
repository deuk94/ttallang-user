package com.ttallang.user.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // 액세스토큰 30분으로 설정함.
    private final int EXPIRE_TIME = 60 * 30 * 1000;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts
                .parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(
            String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, Exception {
        // void 타입으로 바꾸고 싶었는데 && 연산자를 쓰고 있어서 안됨.
        // 그렇다고 한 줄로 쓸 수 있는걸 굳이 두 줄로 늘리기는 싫었음.
        String currentUrl = request.getRequestURI();
        try {
            Jwts
                    .parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println(request.getRequestURI());
            log.info(
                    "Expired JWT={}", token);
            log.info("ExpiredJwtException message={}", e.getMessage());
            System.out.println("토큰이 만료되었습니다.");
            throw new IOException(e);
        } catch (Exception e) {
            log.info("Exception message={}", e.getMessage());
            System.out.println("토큰 검증 과정에서 예외가 발생했습니다.");
            throw new Exception(e);
        }
    }

    public String extractAccessTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            // 옵셔널 객체로 만들어서 null 값에 대한 안정성 확보.
            Optional<Cookie> optionalCookie = Arrays
                    .stream(cookies)
                    .filter(cookie -> {
                        String cookieName = cookie.getName();
                        return cookieName.equals("access_token");
                    })
                    .findFirst();
            // 옵셔널쿠키 객체가 Cookie 객체를 가지고 있으면 거기에 대해 getValue 메서드 적용.
            return optionalCookie.map(Cookie::getValue).orElse(null);
        } else {
            return null;
        }
    }
}