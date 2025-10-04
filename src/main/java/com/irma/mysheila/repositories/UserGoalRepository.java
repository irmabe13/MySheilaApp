package com.irma.mysheila.repositories;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.irma.mysheila.entities.UserGoal;
import com.irma.mysheila.entities.UsersGoalsId;

public interface UserGoalRepository extends JpaRepository<UserGoal, UsersGoalsId> {
    @Transactional
    void deleteByIdUser(Integer idUser);
}
