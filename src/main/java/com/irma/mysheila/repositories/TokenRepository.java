package com.irma.mysheila.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.irma.mysheila.entities.Token;
import com.irma.mysheila.enums.TokenType;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    int revokeAllActiveByIdUser(Integer idUser);

    Optional<Token> findByTokenAndTokenTypeAndRevokedFalseAndExpiredFalse(
            String token, TokenType tokenType);
}
