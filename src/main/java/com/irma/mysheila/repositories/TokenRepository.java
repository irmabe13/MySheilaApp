package com.irma.mysheila.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.irma.mysheila.entities.Token;
import com.irma.mysheila.enums.TokenType;

public interface TokenRepository extends JpaRepository<Token, Integer> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
                    UPDATE Token t
                       SET t.revoked = true,
                           t.expired = true
                     WHERE t.user.idUser = :idUser
                       AND t.revoked = false
                       AND t.expired = false
                    """)
  int revokeAllActiveByUser(@Param("idUser") Integer idUser);

  Optional<Token> findByTokenAndTokenTypeAndRevokedFalseAndExpiredFalse(
      String token, TokenType tokenType);
}
