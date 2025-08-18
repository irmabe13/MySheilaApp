package com.irma.mysheila.entities;


import com.irma.mysheila.enums.FrequencyType;
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
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency_type", nullable = false)
    private FrequencyType frequencyType;

    @Column(name = "frequency_count", nullable = false)
    private int frequencyCount;

    @Column(name = "default_duration_minutes", nullable = false)
    private int defaultDurationMinutes;
}
