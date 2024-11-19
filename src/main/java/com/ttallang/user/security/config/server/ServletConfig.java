package com.ttallang.user.security.config.server;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {
    // 시큐리티 콘피그에서도 처리할 수 있긴 하다.
    // 근데 스프링 시큐리티 레벨에서 처리하는 것보다 톰캣 레벨에서 리다이렉트 처리해주는 것이 성능 오버헤드가 더 적다...

    @Value("${server.port.http}")
    private int serverPortHttp;

    @Value("${server.port}")
    private int serverPortHttps;

    @Bean
    public ServletWebServerFactory serverFactory() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(org.apache.catalina.Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL"); // https 강제 적용...
                SecurityCollection securityCollection = new SecurityCollection(); // 새로운 보안 규칙을 만들기.
                securityCollection.addPattern("/*"); // 보안 패턴 정해놓고 (모든 url 에 대해 정하고,)
                securityConstraint.addCollection(securityCollection); // 그 정해놓은 패턴을 등록.
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(createStandardConnector());
        return tomcat;
    }

    private Connector createStandardConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(serverPortHttp);
        connector.setSecure(false);
        connector.setScheme("http"); // http 요청에 대해서,
        connector.setRedirectPort(serverPortHttps); // https 로 돌려준다.
        return connector;
    }
}