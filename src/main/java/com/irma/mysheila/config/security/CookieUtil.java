package com.irma.mysheila.config.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${app.security.cookie-name}")
    private String cookieName;
    @Value("${app.security.cookie-domain}")
    private String domain;
    @Value("${app.security.cookie-secure}")
    private boolean secure;
    @Value("${app.security.cookie-samesite}")
    private String sameSite;

    public void writeToken(HttpServletResponse response, String token, int maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(secure)
                .domain(domain)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite(sameSite)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }


    public void clearToken(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(secure)
                .domain(domain)
                .path("/")
                .maxAge(0)
                .sameSite(sameSite)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
