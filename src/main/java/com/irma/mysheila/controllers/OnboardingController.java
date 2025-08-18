package com.irma.mysheila.controllers;


import com.irma.mysheila.dto.AvailabilityDto;
import com.irma.mysheila.entities.Availability;
import com.irma.mysheila.entities.User;
import com.irma.mysheila.entities.UserGoal;
import com.irma.mysheila.repositories.AvailabilityRepository;
import com.irma.mysheila.repositories.CategoryRepository;
import com.irma.mysheila.repositories.GoalRepository;
import com.irma.mysheila.repositories.UserGoalRepository;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final CategoryRepository categoryRepo;
    private final GoalRepository goalRepo;
    private final UserGoalRepository userGoalRepo;
    private final AvailabilityRepository availabilityRepo;

    @GetMapping("/categories")
    public Object categories() { return categoryRepo.findAll(); }

    @GetMapping("/goals")
    public Object goal(@RequestParam(required = false) Long categoryId) {
        return categoryId == null ? goalRepo.findAll() : goalRepo.findByCategoryId(categoryId);
    }

    @PostMapping("/user-goals")
    public ResponseEntity<?> setUserGoals(@AuthenticationPrincipal User user, @RequestBody @NotEmpty List<Long> goalIds) {

        userGoalRepo.findByUser(user).forEach(ug -> userGoalRepo.deleteById(ug.getId()));
        goalIds.forEach(id -> userGoalRepo.save(UserGoal.builder().user(user).goal(goalRepo.getReferenceById(id)).build()));
        return ResponseEntity.ok(Map.of("message", "user-goals-updated"));
    }

    @PostMapping("/availabilities")
    public ResponseEntity<?> setAvailabilities(@AuthenticationPrincipal User user, @RequestBody @NotEmpty List<AvailabilityDto> items) {
        availabilityRepo.findByUser(user).forEach(a -> availabilityRepo.deleteById(a.getId()));
        items.forEach(i -> availabilityRepo.save(Availability.builder().user(user).dayOfWeek(i.dayOfWeek()).slot(com.irma.mysheila.enums.DaySlot.valueOf(i.slot())).build()));
        return ResponseEntity.ok(Map.of("message", "availabilities-updated"));
    }
}
