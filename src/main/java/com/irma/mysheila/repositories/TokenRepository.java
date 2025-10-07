package com.irma.mysheila.repositories;

import com.irma.mysheila.entities.Token;
import com.irma.mysheila.enums.TokenType;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    @Modifying
    @Transactional
    @Query(
            "UPDATE Token t SET t.revoked = true, t.expired = true "
                    + "WHERE t.user.idUser = :idUser "
                    + "AND (t.revoked = false OR t.expired = false) "
                    + "AND t.tokenType = com.irma.mysheila.enums.TokenType.REFRESH"
    )
    void revokeAllActiveTokensByIdUser(Integer idUser);

    Optional<Token> findByTokenAndTokenType(String token, TokenType tokenType);

    @Query(value = """
            SELECT t FROM Token t
            INNER JOIN t.user u
            WHERE u.idUser = :idUser AND t.expired = false AND t.revoked = false
            """)
    List<Token> findAllValidTokensByUser(Integer idUser);
}
