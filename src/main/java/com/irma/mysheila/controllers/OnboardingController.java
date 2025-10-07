package com.irma.mysheila.controllers;

import com.irma.mysheila.dto.UserGoalSelectionRequest;
import com.irma.mysheila.dto.WeeklyAvailabilityRequest;
import com.irma.mysheila.services.OnboardingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;


    @GetMapping("/status")
    public ResponseEntity<?> getUserStatus() {
        return ResponseEntity.ok(onboardingService.getUserStatus());
    }

    @GetMapping("/goals")
    public ResponseEntity<?> getGoals() {
        return ResponseEntity.ok(onboardingService.getAvailableGoals());
    }

    // alias pour POST /api/onboarding/goals
    @PostMapping("/goals")
    public ResponseEntity<?> saveGoalsAlias(@Valid @RequestBody UserGoalSelectionRequest request) {
        onboardingService.saveSelectedGoals(request);
        return ResponseEntity.ok("Objectifs sauvegardés.");
    }

    // alias pour POST /api/onboarding/availabilities
    @PostMapping("/availability/save")
    public ResponseEntity<?> saveAvailability(@Valid @RequestBody WeeklyAvailabilityRequest request) {
        try {
            onboardingService.saveWeeklyAvailability(request.getAvailabilities());
            return ResponseEntity.ok("Disponibilités hebdomadaires sauvegardées. Onboarding terminé.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // alias pour GET /api/onboarding/availabilities
    @GetMapping("/availabilities")
    public ResponseEntity<?> getAvailabilitiesAlias() {
        return ResponseEntity.ok(onboardingService.getWeeklyAvailability());
    }
}
