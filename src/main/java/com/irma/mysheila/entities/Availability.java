package com.irma.mysheila.entities;


import com.irma.mysheila.enums.DaySlot;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "availabilities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DaySlot slot;
}
