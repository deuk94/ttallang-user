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
        log.info("access token={} | currentUrl={}", accessToken, currentUrl);

        // 정적 파일 요청 경로.
        boolean staticPath =
                currentUrl.contains("/images") ||
                currentUrl.contains("/js") ||
                currentUrl.contains("/css");
        // 로그인, 회원가입, 아이디/패스워드 찾기 경로.
        boolean accountPath =
                currentUrl.equals("/login/form") ||
                currentUrl.contains("/signup") ||
                currentUrl.contains("/oauth2") ||
                currentUrl.contains("/find") ||
                currentUrl.contains("/phoneAuth");
        /*
        * (구버전 주석이므로 아래 내용은 해당 없음.)
        * 이렇게 분리시켜놓은 이유는 정적 파일은 토큰과 관계 없이 통과시켜줘야 함.
        * 정적 파일들이 유저에게 도착하지 못하고 이미지가 깨지면서 흰 화면이 나오는 현상이 있었음.
        * 이와 관련해서 네트워크 요청쪽을 살펴보니 요청이 단건으로 한 번에 통짜로 묶여서 가는게 아니라,
        * 유저가 새로고침 등을 하면 요청이 예닐곱 개 정도로 나뉘어서 비순차적으로 쪼개져 들어가는 것 같음.
        * 이때 쪼개져 들어가는 요청 중 css, js, images 관련 요청들이 포함되어 있는데,
        * 이것들이 각각 /css, /js, /images 라는 경로로 들어감.
        * 그런데 url 조건을 갖고 분기시키기 때문에 해당하는 url 이 아닌 경우는 필터링 되어버림.
        * 따라서 필수 정적 파일에 해당하는 요청은 무조건 통과시켜주는 절차가 필요함.
        * */

        /*
        * 위 주석처럼 처음에는 if/else 로 나눠서 static file 에 해당하는 경로는 무조건 허용했는데,
        * 그렇게 하니까 SecurityConfig 의 103 라인인 authenticationEntryPoint 메서드에서 에러로 잡혀서 강제로 로그인 페이지로 이동함.
        * 이 말이 무슨 뜻이냐면 로그인절차(인증절차) 이후에 나오는 main 등의 페이지들에서 요구하는 css, js 파일들이 시큐리티에 막혀서 /login/form 경로로 방출되어버리는 상황임.
        * 즉, 로그인 이후 회원 전용 페이지들에서 css, js 등을 못 불러오는 현상이 발생함.
        * 위 파일들은 정적 파일이라 모든 경로에서 필요한데 왜 안됐을까 생각해보니,
        * 인증 절차 없이 무조건적으로 단독 통과시키려고 하니까 /js... 등의 경로를 가지는 파일들을 시큐리티에서 인증 안됐다고 제한을 걸어버림.
        * 따라서 정적 파일에 해당하는 요청들도 authentication 객체를 만들어서 등록해주는 절차(아래 87-89 라인에 해당하는 절차)가 필요한 것 같다.
        * */
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
                    String newAccessToken = tokenUtil.updateToken(accessToken);
                    tokenUtil.setTokenAsCookie(response, newAccessToken);
                    if (accountPath) { // 유효한 상태에서 비회원 관련 페이지로 이동하려는 경우,
                        response.sendRedirect("/main"); // 메인 페이지로 강제이동.
                        return;
                    }
                }
                filterChain.doFilter(request, response);
                // 액세스 토큰이 유효하지 않은 경우는 아래의 예외로 처리됨.
            } catch (IOException ioException) { // 기한 만료의 경우,
                if (!accountPath && !staticPath) { // 계정 관련 경로도 아니고 정적 파일 요청도 아닌 경우,
                    if (currentUrl.equals("/api/login")) {
                        filterChain.doFilter(request, response); // 그렇지만 로그인을 새로 하려는 경우라면 계속 진행.
                    } else {
                        String encodedMessage = URLEncoder.encode("로그인이 만료되었습니다.", StandardCharsets.UTF_8);
                        response.sendRedirect("/login/form?error="+encodedMessage);
                    }
                } else {
                    filterChain.doFilter(request, response); // 로그인 혹은 회원가입 페이지라면 예외 발생시키지 않고 그냥 진행.
                }
            } catch (Exception e) {
                // 어떤 특정 메서드에서 예외 처리가 안되있는 경우 필터 검사 결과로 인해 여기 예외 처리 항목으로 이동될 수 있음.
                log.error("Exception={}", e.getMessage());
                String encodedMessage = URLEncoder.encode("기타 필터링 과정에서 예외가 발생했습니다.", StandardCharsets.UTF_8);
                response.sendRedirect("/login/form?error="+encodedMessage);
            }
        } else { // 액세스 토큰이 없는 경우.
            if (!accountPath && !staticPath) {
                if (!currentUrl.equals("/api/login")) {
                    response.sendRedirect("/login/form"); // 로그인/회원가입 페이지로 이동하거나 로그인하는 경우가 아니면 강제로 로그인 페이지로 이동.
                    return;
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}
