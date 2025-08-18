package com.irma.mysheila.repositories;

import com.irma.mysheila.entities.Availability;
import com.irma.mysheila.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByUser(User user);

}
