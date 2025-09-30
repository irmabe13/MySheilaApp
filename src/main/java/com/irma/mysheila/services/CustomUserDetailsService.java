package com.irma.mysheila.services;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.irma.mysheila.entities.User;
import com.irma.mysheila.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return new org.springframework.security.core.userdetails.User(
        user.getEmail(), user.getPassword(), getAuthority(user));
  }

  private Collection<? extends GrantedAuthority> getAuthority(User user) {
    GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getName());
    return List.of(authority);
  }
}
