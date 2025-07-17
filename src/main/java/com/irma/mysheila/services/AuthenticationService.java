package com.irma.mysheila.services;

import com.irma.mysheila.dto.authentication.LoginRequest;
import com.irma.mysheila.dto.authentication.RegisterRequest;
import com.irma.mysheila.entities.Role;
import com.irma.mysheila.entities.User;
import com.irma.mysheila.enums.Gender;
import com.irma.mysheila.exceptions.UserAlreadyExistException;
import com.irma.mysheila.repositories.RoleRepository;
import com.irma.mysheila.repositories.UserRepository;
import com.irma.mysheila.services.interfaces.IAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    private boolean isPasswordValid(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{12,}$";
        return password != null && password.matches(passwordRegex);
    }


    @Override
    public ResponseEntity<String> register(RegisterRequest request) {
        if (this.userRepository.findByEmail(request.email()).isPresent()) {
            throw new UserAlreadyExistException("User already exist : " + request.email());
        }

        final Role defaultRole = this.roleRepository.findByLabel("USER")
                .orElseThrow(() -> new UserAlreadyExistException("User doesn't exist : " + request.email()));

        if (!isPasswordValid(request.password())) {
            throw new IllegalArgumentException("Password must contain at least 12 characters, including an uppercase letter, a lowercase letter, a digit, and a special character.");
        }


        final User user = User.builder()
                .email(request.email())
                .password(this.passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .birthDate(LocalDate.parse(request.birthDate()))
                .gender(Gender.valueOf(request.gender()))
                .role(defaultRole)
                .build();
        this.userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @Override
    public ResponseEntity<String> login(LoginRequest request) {
        if (this.userRepository.findByEmail(request.email()).isEmpty()) {
            throw new UsernameNotFoundException("User not found : " + request.email());
        }

        final User user = this.userRepository.findByEmail(request.email()).get();

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }

        return ResponseEntity.ok("User logged in successfully");

    }
}
