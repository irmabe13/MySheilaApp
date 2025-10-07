package com.irma.mysheila.services;

import com.irma.mysheila.dto.AvailabilityRequest;
import com.irma.mysheila.dto.GoalResponse;
import com.irma.mysheila.dto.UserGoalSelectionRequest;
import com.irma.mysheila.dto.UserStatusResponse;
import com.irma.mysheila.entities.Availability;
import com.irma.mysheila.entities.Goal;
import com.irma.mysheila.entities.User;
import com.irma.mysheila.entities.UserGoal;
import com.irma.mysheila.entities.UserGoalId;
import com.irma.mysheila.repositories.AvailabilityRepository;
import com.irma.mysheila.repositories.GoalRepository;
import com.irma.mysheila.repositories.UserGoalRepository;
import com.irma.mysheila.repositories.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OnboardingService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final UserGoalRepository userGoalRepository;
    private final AvailabilityRepository availabilityRepository;

    @Transactional
    public List<GoalResponse> getAvailableGoals() {
        return goalRepository.findAll()
                .stream()
                .map(this::toGoalResponse)
                .collect(Collectors.toList());
    }

    /**
     * Sauvegarde la sélection d’objectifs d’un utilisateur (reset puis insert).
     */
    @Transactional
    public void saveSelectedGoals(UserGoalSelectionRequest request) {
        if (request == null || request.getGoalIds() == null || request.getGoalIds().isEmpty()) {
            // Pas d’exception bloquante : tu peux ajuster selon ton besoin
            return;
        }
        final User user = loadCurrentUser();

        // Reset de la sélection précédente
        userGoalRepository.deleteAllByUser(user);

        // Insert des nouvelles sélections (clé composite)
        final List<UserGoal> links = new ArrayList<>();
        for (Integer goalId : request.getGoalIds()) {
            Goal goal = goalRepository.findById(goalId)
                    .orElseThrow(() -> new IllegalArgumentException("Goal introuvable: " + goalId));
            UserGoalId id = new UserGoalId(user.getIdUser(), goal.getIdGoal());
            UserGoal ug = new UserGoal();
            ug.setId(id);
            ug.setUser(user);
            ug.setGoal(goal);
            ug.setAssignedAt(java.time.LocalDate.now());
            ug.setIsActive(Boolean.TRUE);
            ug.setStatus("IN_PROGRESS");
            links.add(ug);
        }
        userGoalRepository.saveAll(links);
    }

    // ==============================
    // ====== ÉTAPE AVAILABILITY =====
    // ==============================

    /**
     * Sauvegarde les disponibilités hebdomadaires :
     * - validation basique (start < end, pas de chevauchement par jour)
     * - reset complet -> insert des nouveaux créneaux
     * - set onboardingComplete = true
     */
    @Transactional
    public void saveWeeklyAvailability(List<AvailabilityRequest> weekly) {
        if (weekly == null || weekly.isEmpty()) {
            throw new IllegalArgumentException("La liste des disponibilités ne peut pas être vide");
        }
        final User user = loadCurrentUser();

        // Validation simple (start < end + pas de chevauchement intra-jour)
        validateWeekly(weekly);

        // Reset puis insert
        availabilityRepository.deleteByUser(user);
        List<Availability> toSave = weekly.stream()
                .filter(Objects::nonNull)
                .map(req -> {
                    Availability a = new Availability();
                    a.setUser(user);
                    // On mappe 1:1 sur les noms attendus par ton repo "findAllByUserOrderByDayAscHourBeginAsc"
                    a.setDay(req.getDayOfWeek());           // String (ex: "MON","TUE","...") ou adapter si enum/int
                    a.setHourBegin(req.getStartTime());     // LocalTime
                    a.setHourEnd(req.getEndTime());         // LocalTime
                    return a;
                })
                .collect(Collectors.toList());
        availabilityRepository.saveAll(toSave);

        // Onboarding terminé
        user.setIsOnboardingComplete(true);
        userRepository.save(user);
    }

    /**
     * Retourne les disponibilités hebdomadaires de l’utilisateur, ordonnées (day asc, hourBegin asc).
     */
    @Transactional
    public List<AvailabilityRequest> getWeeklyAvailability() {
        final User user = loadCurrentUser();
        return availabilityRepository.findAllByUserOrderByDayAscHourBeginAsc(user)
                .stream()
                .map(a -> AvailabilityRequest.builder()
                        .dayOfWeek(a.getDay())
                        .startTime(a.getHourBegin())
                        .endTime(a.getHourEnd())
                        .build())
                .collect(Collectors.toList());
    }

    // ===========================
    // ====== USER / STATUS ======
    // ===========================

    /**
     * Renvoie le statut d’onboarding (true/false) du user courant.
     */
    @Transactional
    public UserStatusResponse getUserStatus() {
        final User user = loadCurrentUser();
        return UserStatusResponse.builder()
                .isOnboardingComplete(Boolean.TRUE.equals(user.getIsOnboardingComplete()))
                .build();
    }

    // ===========================
    // ========= HELPERS =========
    // ===========================

    private GoalResponse toGoalResponse(Goal g) {
        // Gestion NPE minimaliste pour category (selon ton modèle)
        String categoryName = (g.getCategory() != null) ? g.getCategory().getName() : null;

        return GoalResponse.builder()
                .idGoal(g.getIdGoal())
                .name(g.getName())
                .categoryName(categoryName)
                .periodicity(g.getPeriodicity())  // Ex: "DAILY", "WEEKLY"…
                .frequency(g.getFrequency())      // Ex: 3
                .isSelected(false)                // pendant l’onboarding initial, par défaut false
                .build();
    }

    /**
     * Validation basique : start < end + pas de chevauchements par jour.
     */
    private void validateWeekly(List<AvailabilityRequest> weekly) {
        // start < end
        for (AvailabilityRequest r : weekly) {
            if (r == null) {
                continue;
            }
            LocalTime start = r.getStartTime();
            LocalTime end = r.getEndTime();
            if (start == null || end == null || !start.isBefore(end)) {
                throw new IllegalArgumentException("Créneau invalide pour " + r.getDayOfWeek() + " : start < end requis");
            }
        }
        // Chevauchements intra-jour
        weekly.stream()
                .collect(Collectors.groupingBy(AvailabilityRequest::getDayOfWeek))
                .forEach((day, list) -> {
                    List<AvailabilityRequest> sorted = list.stream()
                            .sorted(Comparator.comparing(AvailabilityRequest::getStartTime))
                            .collect(Collectors.toList());
                    for (int i = 1; i < sorted.size(); i++) {
                        AvailabilityRequest prev = sorted.get(i - 1);
                        AvailabilityRequest cur = sorted.get(i);
                        // chevauchement si cur.start < prev.end
                        if (cur.getStartTime().isBefore(prev.getEndTime())) {
                            throw new IllegalArgumentException(
                                    "Chevauchement détecté le " + day + " entre "
                                            + prev.getStartTime() + "-" + prev.getEndTime()
                                            + " et " + cur.getStartTime() + "-" + cur.getEndTime());
                        }
                    }
                });
    }

    /**
     * Récupère l’utilisateur courant via SecurityContext -> UserRepository.
     * Adapte si tu as déjà un service utilitaire (ex: AuthService.getCurrentUser()).
     */
    private User loadCurrentUser() {
        // Ex : si tu stockes l’email dans le principal
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Utilisateur courant introuvable: " + email));
    }
}
