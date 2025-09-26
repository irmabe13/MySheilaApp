package com.irma.mysheila.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plannings_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanningTask {
    @EmbeddedId
    private PlanningsTasksId id;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("idPlanning")
    @JoinColumn(name = "id_planning", nullable = false)
    private Planning planning;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("idTask")
    @JoinColumn(name = "id_task", nullable = false)
    private Task task;
}
