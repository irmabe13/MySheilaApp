package com.irma.mysheila.repositories;

import com.irma.mysheila.entities.PlanningTask;
import com.irma.mysheila.entities.PlanningsTasksId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanningTaskRepository extends JpaRepository<PlanningTask, PlanningsTasksId> {
}
