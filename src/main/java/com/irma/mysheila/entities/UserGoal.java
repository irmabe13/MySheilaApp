package com.irma.mysheila.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGoal {
    @EmbeddedId
    private UsersGoalsId id;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("idUser")
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("idGoal")
    @JoinColumn(name = "id_goal", nullable = false)
    private Goal goal;
}
