package org.example.sor_mrp.shared.configuration.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class CookieService {

    @Value("${jwt.refresh-cookie-name:refreshToken}")
    private String refreshCookieName;

    /**
     * Set Token Cookie
     */
    public void setTokenCookie(HttpServletResponse response, String token, LocalDateTime expiresAt) {
        long secondsUntilExpiry = Duration.between(LocalDateTime.now(), expiresAt).getSeconds();

        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, token)
                .httpOnly(true)
                .secure(false) // Set to true in production when using HTTPS
                .path("/")
                .maxAge(Math.max(secondsUntilExpiry, 0))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * Expire/Clear Token Cookie
     */
    public void expireTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0) // Setting maxAge to 0 instructs the browser to delete the cookie immediately
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
