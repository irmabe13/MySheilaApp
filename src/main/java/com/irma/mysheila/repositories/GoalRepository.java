package com.irma.mysheila.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.irma.mysheila.entities.Goal;

public interface GoalRepository extends JpaRepository<Goal, Integer> {
}
