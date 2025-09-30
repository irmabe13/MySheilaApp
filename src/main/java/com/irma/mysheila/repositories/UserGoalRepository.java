package com.irma.mysheila.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.irma.mysheila.entities.UserGoal;
import com.irma.mysheila.entities.UsersGoalsId;

public interface UserGoalRepository extends JpaRepository<UserGoal, UsersGoalsId> {}
