package com.irma.mysheila.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "plannings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Planning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planning")
    private Integer idPlanning;

    @Column(name = "hour_begin", nullable = false)
    private LocalTime hourBegin;

    @Column(name = "hour_end", nullable = false)
    private LocalTime hourEnd;

    @Column(name = "status", nullable = false, length = 20)
    private String status;
}
