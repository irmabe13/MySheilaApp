package com.irma.mysheila.config.security;

import com.irma.mysheila.repositories.TokenRepository;
import com.irma.mysheila.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final TokenRepository tokenRepo;

    @Value("${app.security.cookie-name}")
    private String cookieName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractTokenFromCookieOrHeader(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.parse(token);
            String email = claims.getSubject();
            String jti = claims.getId();

            var tokenEntity = tokenRepo.findByJti(jti).orElse(null);
            if (tokenEntity == null || tokenEntity.isRevoked()) {
                filterChain.doFilter(request, response);
                return;
            }

            var user = userRepo.findByEmail(email).orElse(null);
            if (user == null) {
                filterChain.doFilter(request, response);
                return;
            }

            String rolesCsv = (String) claims.get("roles");
            List<SimpleGrantedAuthority> authorities = rolesCsv == null ? List.of() : Arrays.stream(rolesCsv.split(","))
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookieOrHeader(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (cookieName.equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return null;
    }
}
