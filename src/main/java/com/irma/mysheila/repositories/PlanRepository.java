package com.irma.mysheila.repositories;

import com.irma.mysheila.entities.Plan;
import com.irma.mysheila.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findFirstByUserAndStatusOrderByIdDesc(User user, String status);
}
