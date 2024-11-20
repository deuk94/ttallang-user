package com.ttallang.user.security.config.handler;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class SessionHandler implements SessionInformationExpiredStrategy {

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
        HttpServletResponse response = event.getResponse();
        String encodedMessage = URLEncoder.encode("다른 기기에서 로그인되어 로그아웃 되었습니다.", StandardCharsets.UTF_8);
        response.sendRedirect("/login/form?error="+encodedMessage);
    }
}
