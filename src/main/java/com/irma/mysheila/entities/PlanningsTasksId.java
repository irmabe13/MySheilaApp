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
public class PlanningsTasksId implements Serializable {
    private Integer idPlanning;
    private Integer idTask;
}
