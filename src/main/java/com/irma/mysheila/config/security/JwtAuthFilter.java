package com.irma.mysheila.config.security;

import com.irma.mysheila.entities.Token;
import com.irma.mysheila.repositories.TokenRepository;
import com.irma.mysheila.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        if (!jwtService.isValidToken(jwt)) {
            log.warn("Tentative d'accès avec un JWT invalide (signature ou expiration).");
            filterChain.doFilter(request, response);
            return;
        }

        username = jwtService.extractUsernameFromToken(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            log.info("Chargement de l'utilisateur {} réussi.", username);

            boolean isJwtValid = jwtService.validateTokenForUser(jwt, userDetails);

            // 2. Vérification BDD (Statut du Token)
            Optional<Token> tokenOptional = tokenRepository.findByToken(jwt);

            // Initialisation à false
            boolean isTokenActiveInDb = false;

            if (tokenOptional.isPresent()) {
                Token token = tokenOptional.get();
                // Mise à jour de la variable isTokenActiveInDb
                isTokenActiveInDb = !token.isExpired() && !token.isRevoked();

                // Logs pour le débogage de la base de données
                log.info("Token BDD trouvé. Expired={}, Revoked={}", token.isExpired(), token.isRevoked());

            } else {
                log.warn("ERREUR DE BDD: Le Token n'a pas été trouvé dans le TokenRepository.");
                // Si le token n'est pas trouvé, isTokenActiveInDb reste false
            }

            // --- LOGS DE DEBUG CRITIQUES ---
            log.info("JWT Validation (isJwtValid): {}", isJwtValid);
            log.info("DB Status (isTokenActiveInDb): {}", isTokenActiveInDb);

            if (tokenOptional.isPresent()) {
                Token token = tokenOptional.get();
                log.info("Token BDD: Expired={}, Revoked={}", token.isExpired(), token.isRevoked());
            } else {
                log.warn("Token non trouvé dans la BDD malgré le log Hibernate.");
            }

            if (isJwtValid && isTokenActiveInDb) {
                log.info("AUTHENTIFICATION RÉUSSIE pour l'utilisateur: {}", username);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                log.warn("AUTHENTIFICATION ÉCHOUÉE: Contexte non établi pour {}. Vérifiez les logs ci-dessus.", username);
            }
        }
        filterChain.doFilter(request, response);
    }
}
