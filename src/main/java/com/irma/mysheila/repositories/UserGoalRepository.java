package com.irma.mysheila.repositories;

import com.irma.mysheila.entities.User;
import com.irma.mysheila.entities.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {

    List<UserGoal> findByUser(User user);

}
