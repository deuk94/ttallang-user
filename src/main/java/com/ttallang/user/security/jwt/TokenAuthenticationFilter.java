package com.ttallang.user.security.jwt;

import com.ttallang.user.security.config.auth.PrincipalDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenUtil tokenUtil;
    private final PrincipalDetailsService principalDetailsService;

    public TokenAuthenticationFilter(
            TokenUtil tokenUtil,
            PrincipalDetailsService principalDetailsService
    ) {
        this.tokenUtil = tokenUtil;
        this.principalDetailsService = principalDetailsService;
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

        String accessToken = tokenUtil.extractAccessTokenFromRequest(request);
        String currentUrl = request.getRequestURI();
        log.info("access token={}", accessToken);

        if (accessToken != null) { // 액세스 토큰이 있다면 검증해야 함.
            try {
                if (tokenUtil.validateToken(accessToken, request, response)) { // 유효한 토큰인 경우에만,
                    String username = tokenUtil.getUsernameFromToken(accessToken);
                    log.info("username={}", username);
                    /*
                     * 원래는 token 안에 권한 정보가 있어야 함.
                     * 그 권한 정보를 토큰에서 추출해서 그걸 컨텍스트홀더에 저장해놓고 필요한 처리를 하는 것.
                     * 근데 왠지 username을 받은 다음에 그걸로 db 참조해서 권한 정보 찾아오는 것도 가능할 것 같아서 실험해봄.
                     * */
                    UserDetails userDetails = principalDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    if (currentUrl.equals("/login/form")) { // 유효한 상태에서 로그인 페이지로 이동하려는 경우,
                        response.sendRedirect("/main"); // 메인 페이지로 강제이동.
                        return;
                    }
                } // 액세스 토큰이 유효하지 않은 경우는 아래의 예외로 처리됨.
                filterChain.doFilter(request, response);
            } catch (IOException ioException) {
                String encodedMessage = URLEncoder.encode("토큰이 만료되었습니다.", StandardCharsets.UTF_8);
                response.sendRedirect("/login/form?error="+encodedMessage);
            } catch (Exception e) {
                String encodedMessage = URLEncoder.encode("토큰 검증 과정에서 예외가 발생했습니다.", StandardCharsets.UTF_8);
                response.sendRedirect("/login/form?error="+encodedMessage);
            }
        } else { // 액세스 토큰이 없는 경우.
            if (!currentUrl.contains("/image") && !currentUrl.contains("/js") && !currentUrl.contains("/css") && !currentUrl.contains("/login/form")) {
                if (!currentUrl.equals("/api/login")) {
                    response.sendRedirect("/login/form"); // 로그인 페이지로 이동하거나 로그인하는 경우가 아니면 강제로 로그인 페이지로 이동.
                    return;
                }
            } // 로그인 페이지로 이동.
            filterChain.doFilter(request, response);
        }
    }
}
