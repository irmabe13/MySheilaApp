package com.irma.mysheila.repositories;

import com.irma.mysheila.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}
