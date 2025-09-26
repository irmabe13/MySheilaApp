package com.irma.mysheila.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UsersGoalsId implements Serializable {
    private Integer idUser;
    private Integer idGoal;
}
