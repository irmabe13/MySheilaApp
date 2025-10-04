package com.irma.mysheila.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.irma.mysheila.entities.Planning;

public interface PlanningRepository extends JpaRepository<Planning, Integer> {
}
