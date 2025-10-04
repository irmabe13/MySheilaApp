package com.irma.mysheila.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.irma.mysheila.entities.PlanningTask;
import com.irma.mysheila.entities.PlanningsTasksId;

public interface PlanningTaskRepository extends JpaRepository<PlanningTask, PlanningsTasksId> {
}
