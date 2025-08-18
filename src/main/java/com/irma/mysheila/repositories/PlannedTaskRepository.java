package com.irma.mysheila.repositories;

import com.irma.mysheila.entities.PlannedTask;
import com.irma.mysheila.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PlannedTaskRepository extends JpaRepository<PlannedTask, Long> {

    List<PlannedTask> findByUserAndTaskDateBetweenOrderByTaskDateAsc(User user, LocalDate start, LocalDate end);
}
