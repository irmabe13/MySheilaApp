package com.irma.mysheila.repositories;

import com.irma.mysheila.entities.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByCategoryId(Long categoryId);

}
