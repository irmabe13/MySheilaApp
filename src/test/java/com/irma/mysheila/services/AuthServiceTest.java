package com.irma.mysheila.services;


import com.irma.mysheila.dto.RegisterRequest;
import com.irma.mysheila.entities.Role;
import com.irma.mysheila.entities.User;
import com.irma.mysheila.repositories.RoleRepository;
import com.irma.mysheila.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_NewUser_Success() {
        RegisterRequest request = new RegisterRequest("test@mail.com", "password", "John", "Doe");
        Role userRole = new Role(1, "USER");
        String encodedPassword = "encodedPassword123";

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> authService.register(request));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(request.getEmail(), savedUser.getEmail());
        assertEquals(encodedPassword, savedUser.getPassword());
        assertEquals(request.getFirstname(), savedUser.getFirstname());
        assertEquals(userRole, savedUser.getRole());
        assertTrue(savedUser.getEnabled());

        verify(userRepository).existsByEmail(request.getEmail());
        verify(roleRepository).findByName("USER");
        verify(passwordEncoder).encode(request.getPassword());
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        RegisterRequest request = new RegisterRequest("existing@mail.com", "password", "John", "Doe");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}
