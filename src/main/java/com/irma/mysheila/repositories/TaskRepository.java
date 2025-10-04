package com.irma.mysheila.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.irma.mysheila.entities.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}
