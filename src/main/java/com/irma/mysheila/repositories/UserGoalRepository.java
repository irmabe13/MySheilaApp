package com.irma.mysheila.repositories;

import com.irma.mysheila.entities.User;
import com.irma.mysheila.entities.UserGoal;
import com.irma.mysheila.entities.UserGoalId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserGoalRepository extends JpaRepository<UserGoal, UserGoalId> {

    List<UserGoal> findAllByUser(User user);

    @Modifying
    @Query("DELETE FROM UserGoal ug WHERE ug.user = :user")
    void deleteAllByUser(@Param("user") User user);
}
