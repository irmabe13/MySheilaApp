package com.irma.mysheila.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGoalId implements Serializable {
    @Column(name = "id_user")
    private Integer idUser;

    @Column(name = "id_goal")
    private Integer idGoal;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserGoalId that = (UserGoalId) o;
        return Objects.equals(idUser, that.idUser) && Objects.equals(idGoal, that.idGoal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, idGoal);
    }
}
