package com.irma.mysheila.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_goal") private Integer idGoal;

    @Column(name = "name", nullable = false) private String name;
    @Column(name = "periodicity", nullable = false, length = 50) private String periodicity;
    @Column(name = "frequency", nullable = false) private Integer frequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_category", nullable = false)
    private Category category;
}
