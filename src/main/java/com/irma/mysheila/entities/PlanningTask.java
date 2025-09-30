package com.irma.mysheila.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "plannings_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanningTask {
  @EmbeddedId private PlanningsTasksId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("idPlanning")
  @JoinColumn(name = "id_planning", nullable = false)
  private Planning planning;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("idTask")
  @JoinColumn(name = "id_task", nullable = false)
  private Task task;
}
