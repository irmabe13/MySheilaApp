package com.irma.mysheila.repositories;

import com.irma.mysheila.entities.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Integer> {
}
