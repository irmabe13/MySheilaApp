package com.irma.mysheila.repositories;

import com.irma.mysheila.entities.UserGoal;
import com.irma.mysheila.entities.UsersGoalsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGoalRepository extends JpaRepository<UserGoal, UsersGoalsId> {
}
