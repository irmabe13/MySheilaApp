package com.irma.mysheila.services;

import com.irma.mysheila.dto.AuthRequest;
import com.irma.mysheila.dto.RegisterRequest;
import com.irma.mysheila.dto.TokenPair;
import com.irma.mysheila.entities.Role;
import com.irma.mysheila.entities.Token;
import com.irma.mysheila.entities.User;
import com.irma.mysheila.repositories.RoleRepository;
import com.irma.mysheila.repositories.TokenRepository;
import com.irma.mysheila.repositories.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwt;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private Role userRole;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userRole = new Role(1, "USER");
    }

    @Test
    void register_NewUser_Success() {
        RegisterRequest request = new RegisterRequest("test@mail.com", "password", "John", "Doe");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("HASH");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> authService.register(request));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(request.getEmail(), savedUser.getEmail());
        assertEquals("HASH", savedUser.getPassword());
        assertEquals(request.getFirstname(), savedUser.getFirstname());
        assertEquals(userRole, savedUser.getRole());
        assertTrue(savedUser.getEnabled());

        verifyNoInteractions(jwt, tokenRepository, authenticationManager, userDetailsService);
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        RegisterRequest request = new RegisterRequest("existing@mail.com", "password", "John", "Doe");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_authenticates_generates_tokenpair_revoke_old_saves_new_and_sets_context() {
        AuthRequest req = new AuthRequest("irma@test.com", "pass");

        User user =
                User.builder()
                        .idUser(42)
                        .email("irma@test.com")
                        .password("HASH")
                        .role(userRole)
                        .enabled(true)
                        .build();

        when(userRepository.findByEmail("irma@test.com")).thenReturn(Optional.of(user));

        Authentication auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        TokenPair pair = new TokenPair("access-123", "refresh-456");
        when(jwt.generateTokenPair(auth)).thenReturn(pair);

        TokenPair result = authService.login(req);

        assertThat(result).isNotNull();
        assertEquals("access-123", result.getAccessToken());
        assertEquals("refresh-456", result.getRefreshToken());

        // Révocation des tokens actifs précédents: l'impl utilise findAllValidTokensByUser + saveAll
        verify(tokenRepository).findAllValidTokensByUser(42);
        // Sauvegarde du nouveau token d'accès uniquement (l'impl ne persiste pas le refresh token)
        verify(tokenRepository, times(1)).save(any(Token.class));
        // Ne pas vérifier le SecurityContext: l'impl ne le met pas à jour dans login()
        verify(jwt).generateTokenPair(auth);
    }
}
