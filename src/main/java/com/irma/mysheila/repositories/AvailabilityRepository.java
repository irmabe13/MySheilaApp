package com.irma.mysheila.repositories;

import com.irma.mysheila.entities.Availability;
import com.irma.mysheila.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {

    List<Availability> findAllByUserOrderByDayAscHourBeginAsc(User user);

    // Supprime les disponibilités de l'utilisateur
    @Modifying
    @Query("DELETE FROM Availability a WHERE a.user = :user")
    void deleteByUser(@Param("user") User user);
}
