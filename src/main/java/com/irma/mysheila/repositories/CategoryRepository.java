package com.irma.mysheila.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.irma.mysheila.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
