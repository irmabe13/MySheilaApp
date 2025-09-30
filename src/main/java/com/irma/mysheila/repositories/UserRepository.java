package com.irma.mysheila.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.irma.mysheila.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);
}
