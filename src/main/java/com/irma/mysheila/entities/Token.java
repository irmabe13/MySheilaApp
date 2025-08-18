package com.irma.mysheila.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String jti;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean revoked;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}
